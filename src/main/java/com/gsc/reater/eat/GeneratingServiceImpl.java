package com.gsc.reater.eat;

import com.gsc.reater.model.Node;
import com.gsc.reater.model.ReaterModel;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GeneratingServiceImpl implements GeneratingService {

    //https://github.com/aciapetti/opennlp-italian-models/blob/master/lang/it/POS/tagsDictionaryIt.txt
    private final static List<String> IT_POS_PUNCTUATION = List.of("FC", "FF", "FS");
    private final static List<String> IT_POS_BAL_PUNCT = List.of("FB");

    @Override
    public List<String> generateSentences(String pathToModelFile, int numberOfSentences) {
        ReaterModel model = readModelFile(pathToModelFile);
        System.out.println(model);

        List<String> sentences = new ArrayList<>(numberOfSentences);
        for (int i = 0; i < numberOfSentences; i++) {
            sentences.add(generateSentence(model));
        }

        return sentences;
    }

    private String generateSentence(ReaterModel model) {
        StringBuilder sentence = new StringBuilder();
        Node currentToken = chooseEntryPoint(model);
        appendToSentence(sentence, currentToken, null);
        while (currentToken.getLinks().size() > 0) {
            Node previousToken = currentToken;
            currentToken = chooseNextToken(currentToken, model);
            appendToSentence(sentence, currentToken, previousToken);
        }
        return sentence.toString();
    }

    private Node chooseNextToken(Node currentNode, ReaterModel model) {
        List<Node> nextTokenCandidates = new ArrayList<>();
        for (String l : currentNode.getLinks().keySet()) {
            for (int i = 0; i < currentNode.getLinks().get(l); i++)
                nextTokenCandidates.add(model.getTokensNetwork().deepSearch(l));
        }
        return nextTokenCandidates.get(
                randomToMax(nextTokenCandidates.size() - 1)
        );
    }

    private Node chooseEntryPoint(ReaterModel model) {
        List<Node> entryPointCandidates = new ArrayList<>();
        for (String entryPointContent : model.getTokensNetwork().getEntryPoints().keySet())
            for (int i = 0; i < model.getTokensNetwork().getEntryPoints().get(entryPointContent); i++)
                entryPointCandidates.add(model.getTokensNetwork().deepSearch(entryPointContent));
        return entryPointCandidates.get(
                randomToMax(entryPointCandidates.size() - 1)
        );
    }

    private ReaterModel readModelFile(String pathToModelFile) {
        ReaterModel model = null;
        try {
            FileInputStream fi = new FileInputStream(pathToModelFile);
            ObjectInputStream oi = new ObjectInputStream(fi);
            model = (ReaterModel) oi.readObject();
            oi.close();
            fi.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return model;
    }

    private int randomToMax(int max) {
        return ThreadLocalRandom.current().nextInt(0, max + 1);
    }

    private void appendToSentence(StringBuilder sentence, Node currentToken, Node previousToken) {
        if (GeneratingServiceImpl.IT_POS_PUNCTUATION.contains(currentToken.getMetaData().get("posTag"))
                || currentToken.getContent().equals("'")) // connect apostrophe
            sentence.setLength(sentence.length() - 1);
        sentence.append(currentToken.getContent());
        if (!GeneratingServiceImpl.IT_POS_BAL_PUNCT.contains(currentToken.getMetaData().get("posTag"))
                || (previousToken.getContent().equals("po") && currentToken.getContent().equals("'")))
            sentence.append(" ");
    }
}
