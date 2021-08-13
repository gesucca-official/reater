package com.gsc.reater.service.shit.sub;

import com.gsc.reater.model.Network;
import com.gsc.reater.model.Node;
import com.gsc.reater.model.ReaterModel;
import com.gsc.reater.service.gen.GeneralMathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SentenceBuildingServiceImpl implements SentenceBuildingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //https://github.com/aciapetti/opennlp-italian-models/blob/master/lang/it/POS/tagsDictionaryIt.txt
    private final static List<String> IT_POS_PUNCTUATION = List.of("FC", "FF", "FS");
    private final static List<String> IT_POS_BAL_PUNCT = List.of("FB");
    private final static String IT_SENTENCE_BOUNDARY_PUNCT = "FS";

    private final GeneralMathService mathService;

    @Autowired
    public SentenceBuildingServiceImpl(GeneralMathService mathService) {
        this.mathService = mathService;
    }

    @Override
    public Node chooseEntryPoint(ReaterModel model) {
        log.info("Choosing Entry Point...");

        List<Node> entryPointCandidates = new ArrayList<>();
        for (String entryPointContent : model.getTokensNetwork().getEntryPoints().keySet())
            for (int i = 0; i < model.getTokensNetwork().getEntryPoints().get(entryPointContent); i++)
                entryPointCandidates.add(model.getTokensNetwork().deepSearch(entryPointContent));
        log.info("Candidates (multiplied by weight): " + prettifyCandidates(entryPointCandidates));

        Node chosen = entryPointCandidates.get(mathService.randomToMax(entryPointCandidates.size() - 1));
        log.info("Chosen: " + chosen.getContent());
        return chosen;
    }

    @Override
    public Node chooseNextToken(Node currentNode, ReaterModel model) {
        log.info("Choosing Next Node... (current=" + currentNode.getContent() + ")");

        List<Node> nextTokenCandidates = new ArrayList<>();
        for (String l : currentNode.getLinks().keySet()) {
            for (int i = 0; i < currentNode.getLinks().get(l); i++)
                nextTokenCandidates.add(model.getTokensNetwork().deepSearch(l));
        }
        log.info("Candidates (multiplied by weight): " + prettifyCandidates(nextTokenCandidates));

        Node chosen = nextTokenCandidates.get(mathService.randomToMax(nextTokenCandidates.size() - 1));
        log.info("Chosen: " + chosen.getContent());
        return chosen;
    }

    @Override
    public boolean hasTerminalLinks(Node node, Network network) {
        for (String l : node.getLinks().keySet())
            if (isTerminal(network.deepSearch(l)))
                return true;
        return false;
    }

    @Override
    public Node chooseAmongTerminalLinks(Node node, Network network) {
        log.info("Choosing Terminal Node... (current=" + node.getContent() + ")");

        List<Node> candidates = new ArrayList<>();
        for (String l : node.getLinks().keySet())
            if (isTerminal(network.deepSearch(l)))
                for (int i = 0; i < node.getLinks().get(l); i++)
                    candidates.add(network.deepSearch(l));
        log.info("Candidates (multiplied by weight): " + prettifyCandidates(candidates));

        Node chosen = candidates.get(mathService.randomToMax(candidates.size() - 1));
        log.info("Chosen: " + chosen.getContent());
        return chosen;
    }

    @Override
    public void localizedAppend(Node currentNode, Node previousNode, StringBuilder sentence, SupportedLanguages lang) {
        if (SentenceBuildingServiceImpl.IT_POS_PUNCTUATION.contains(currentNode.getMetaData().get("posTag"))
                || currentNode.getContent().equals("'")) // connect apostrophe
            sentence.setLength(sentence.length() - 1);
        sentence.append(currentNode.getContent());
        if (!SentenceBuildingServiceImpl.IT_POS_BAL_PUNCT.contains(currentNode.getMetaData().get("posTag"))
                || (currentNode.getContent().equals("po") && currentNode.getContent().equals("'")))
            sentence.append(" ");
        log.info("Sentence so far: " + sentence);
    }

    private String prettifyCandidates(List<Node> candidates) {
        return candidates.stream()
                .map(Node::getContent)
                .map(n -> n + "(x1)")
                .reduce((c1, c2) -> {
                    String radix1 = c1.substring(c1.lastIndexOf(",") + 1, c1.lastIndexOf("(x"));
                    String radix2 = c2.substring(0, c2.lastIndexOf("(x"));
                    int qty = Integer.parseInt(c1.substring(c1.lastIndexOf("(x") + 2, c1.length() - 1));
                    if (radix1.equals(radix2))
                        return c1.substring(0, c1.lastIndexOf("(x") + 2) + (qty + 1) + ")";
                    else return c1 + "," + c2;
                }).orElse("ERROR PRETTIFYING CANDIDATES");
    }

    private boolean isTerminal(Node n) {
        return n.getMetaData().get("posTag").equals(SentenceBuildingServiceImpl.IT_SENTENCE_BOUNDARY_PUNCT);
    }
}
