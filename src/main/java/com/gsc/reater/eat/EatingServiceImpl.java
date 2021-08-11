package com.gsc.reater.eat;

import com.gsc.reater.model.Network;
import com.gsc.reater.model.Node;
import com.gsc.reater.model.ReaterModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EatingServiceImpl implements EatingService {

    private static final String NO_INPUT_FILE_CONTENT = "NO CONTENT";

    private final ReaterModel model = new ReaterModel();

    @Override
    public void eat(String pathToInput, String pathToOutput) {
        String input = readFile(pathToInput);
        if (input.equals(EatingServiceImpl.NO_INPUT_FILE_CONTENT))
            throw new InputMismatchException("Invalid or Empty Input File");

        List<String> sentences = extractSentences(input);
        for (String sentence : sentences)
            digestSentence(sentence);
        writeOutputFile(pathToOutput);
    }

    private String readFile(String pathToInput) {
        String content = EatingServiceImpl.NO_INPUT_FILE_CONTENT;
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(pathToInput));
            content = reader.lines().collect(Collectors.joining(" "));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
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
        System.out.println("***********");
        System.out.println("sentence " + sentence);
        List<String> tokens = extractTokens(sentence);
        List<String> posTags = extractPartOfSpeechTags(tokens);

        for (int i = 0; i < tokens.size(); i++) {
            Node tokenNode = new Node(tokens.get(i));
            tokenNode.getMetaData().put("posTag", posTags.get(i));
            addToNetwork(tokenNode, i > 0 ? tokens.get(i - 1) : null, this.model.getTokensNetwork());

            Node posNode = new Node(posTags.get(i));
            addToNetwork(posNode, i > 0 ? posTags.get(i - 1) : null, this.model.getPosNetwork());
        }
    }

    private void addToNetwork(Node node, String prevValue, Network network) {
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

    private void writeOutputFile(String pathToOutput) {
        try {
            FileOutputStream f = new FileOutputStream(pathToOutput);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(model);
            o.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
