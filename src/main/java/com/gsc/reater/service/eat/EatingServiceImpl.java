package com.gsc.reater.service.eat;

import com.gsc.reater.model.Network;
import com.gsc.reater.model.Node;
import com.gsc.reater.model.NodeContent;
import com.gsc.reater.model.ReaterModel;
import com.gsc.reater.service.gen.FileInputOutputService;
import com.gsc.reater.service.gen.GeneralMathService;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EatingServiceImpl implements EatingService {

    private final FileInputOutputService fileInputOutputService;
    private final GeneralMathService mathService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // working variables - is this an antipattern?
    private ReaterModel model;
    private Set<Integer> sentenceLengths;

    @Autowired
    public EatingServiceImpl(FileInputOutputService fileInputOutputService, GeneralMathService mathService) {
        this.fileInputOutputService = fileInputOutputService;
        this.mathService = mathService;
    }

    @Override
    public void eat(String pathToInput, String pathToOutput) {
        this.model = new ReaterModel();
        this.sentenceLengths = new HashSet<>();

        List<String> sentences = extractSentences(fileInputOutputService.readRawTextFile(pathToInput));
        for (String sentence : sentences)
            digestSentence(sentence);

        model.setAvgLength(mathService.avgLength(sentenceLengths));
        model.setLengthsStdDev(mathService.lengthsStdDev(sentenceLengths));

        fileInputOutputService.saveModelFile(pathToOutput, this.model);
    }

    private List<String> extractSentences(String input) {
        InputStream is = getClass().getResourceAsStream("/it-sent.bin");
        try {
            assert is != null;
            SentenceModel model = new SentenceModel(is);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
            return List.of(sentenceDetector.sentDetect(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private void digestSentence(String sentence) {
        log.info("Digesting Sentence: " + sentence);
        List<String> tokens = extractTokens(sentence);
        List<String> posTags = extractPartOfSpeechTags(tokens);
        log.info("Extracted Tokens: " + tokens);
        log.info("Extracted POS Tags: " + posTags);

        this.sentenceLengths.add(tokens.size());

        for (int i = 0; i < tokens.size(); i++) {
            log.info("Processing " + tokens.get(i));
            Node tokenNode = new Node(tokens.get(i), posTags.get(i));
            addPreviousPosChain(tokenNode, posTags, i);
            addToNetwork(tokenNode, i > 0 ? new NodeContent(tokens.get(i - 1), posTags.get(i - 1)) : null, this.model.getNetwork());
            log.info("Created and inserted Node: " + tokenNode);
        }
    }

    private void addPreviousPosChain(Node tokenNode, List<String> posTags, int i) {
        if (i == 0)
            return;
        tokenNode.getPreviousPosChains().add(new ArrayList<>(posTags.subList(0, i)));
    }

    private void addToNetwork(Node node, NodeContent prevValue, Network network) {
        if (prevValue == null)
            network.addEntryPoint(node);
        else {
            network.addNode(node);
            network.deepSearch(prevValue).addLinkTo(node);
        }
    }

    private List<String> extractTokens(String sentence) {
        return List.of(SimpleTokenizer.INSTANCE.tokenize(sentence));
    }

    private List<String> extractPartOfSpeechTags(List<String> tokens) {
        InputStream inputStreamPOSTagger = getClass().getResourceAsStream("/it-pos-maxent.bin");
        try {
            assert inputStreamPOSTagger != null;
            POSModel posModel = new POSModel(inputStreamPOSTagger);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            return List.of(posTagger.tag(tokens.toArray(new String[0])));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }

}
