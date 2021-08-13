package com.gsc.reater.service.shit;

import com.gsc.reater.model.Node;
import com.gsc.reater.model.ReaterModel;
import com.gsc.reater.service.gen.FileInputOutputService;
import com.gsc.reater.service.gen.GeneralMathService;
import com.gsc.reater.service.shit.sub.SentenceBuildingService;
import com.gsc.reater.service.shit.sub.SupportedLanguages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShittingServiceImpl implements ShittingService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final FileInputOutputService fileInputOutputService;
    private final SentenceBuildingService sentenceBuildingService;
    private final GeneralMathService mathService;

    @Autowired
    public ShittingServiceImpl(FileInputOutputService fileInputOutputService, SentenceBuildingService sentenceBuildingService, GeneralMathService mathService) {
        this.fileInputOutputService = fileInputOutputService;
        this.sentenceBuildingService = sentenceBuildingService;
        this.mathService = mathService;
    }

    @Override
    public List<String> generateSentences(String pathToModelFile, int numberOfSentences) {
        Optional<ReaterModel> optionalModel = fileInputOutputService.readModelFile(pathToModelFile);
        if (optionalModel.isEmpty())
            throw new RuntimeException("Could not load Reater Model");

        log.info("\n" + optionalModel.get());

        List<String> sentences = new ArrayList<>(numberOfSentences);
        for (int i = 0; i < numberOfSentences; i++) {
            sentences.add(generateSentence(optionalModel.get()));
        }
        return sentences;
    }

    private String generateSentence(ReaterModel model) {
        StringBuilder sentence = new StringBuilder();
        Node currentNode = sentenceBuildingService.chooseEntryPoint(model);
        sentenceBuildingService.localizedAppend(currentNode, null, sentence, SupportedLanguages.IT);
        int currentSentenceLength = 1;

        while (currentNode.getLinks().size() > 0) {
            Node previousNode = currentNode;
            currentNode = sentenceBuildingService.chooseNextToken(currentNode, model);
            sentenceBuildingService.localizedAppend(currentNode, previousNode, sentence, SupportedLanguages.IT);
            currentSentenceLength++;
            if (forceEndOfSentence(model, sentence, currentNode, currentSentenceLength)) break;
        }
        return sentence.toString();
    }

    private boolean forceEndOfSentence(ReaterModel model, StringBuilder sentence, Node currentNode, int currentSentenceLength) {
        int chance = mathService.randomToMax(100);
        boolean isInStdDevRange = Math.abs(currentSentenceLength - model.getAvgLength()) < model.getLengthsStdDev();
        boolean sentenceCanBeEnded = sentenceBuildingService.hasTerminalLinks(currentNode, model.getTokensNetwork());

        log.info("Sentence Length: " + currentSentenceLength);
        log.info("Is Sentence Length in StdDev Range? " + isInStdDevRange);
        log.info("Sentence Can Be Ended? " + sentenceCanBeEnded);
        log.info("Random Factor: " + chance);

        if (sentenceCanBeEnded)
            if (isInStdDevRange) {
                if (chance < 75)
                    return appendFinalToken(model, sentence, currentNode, "Ending Sentence in StdDev Range due to Chance < 75");
            } else if (chance < 20)
                return appendFinalToken(model, sentence, currentNode, "Ending Sentence NOT in StdDev Range due to Chance < 20");
        return false;
    }

    private boolean appendFinalToken(ReaterModel model, StringBuilder sentence, Node currentNode, String s) {
        log.info(s);
        sentenceBuildingService.localizedAppend(
                sentenceBuildingService.chooseAmongTerminalLinks(currentNode, model.getTokensNetwork()), currentNode, sentence, SupportedLanguages.IT);
        return true;
    }

}
