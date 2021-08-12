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

        log.info(optionalModel.get().toString());

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
            int chance = mathService.randomToMax(100);
            if (Math.abs(currentSentenceLength - model.getAvgLength()) > model.getLengthsStdDev())
                if (chance < 90 && sentenceBuildingService.hasTerminalLinks(currentNode, model.getTokensNetwork())) {
                    sentenceBuildingService.localizedAppend(
                            sentenceBuildingService.chooseAmongTerminalLinks(currentNode, model.getTokensNetwork()), currentNode, sentence, SupportedLanguages.IT);
                    break;
                } else if (chance < 99 && sentenceBuildingService.hasTerminalLinks(currentNode, model.getTokensNetwork())) {
                    sentenceBuildingService.localizedAppend(
                            sentenceBuildingService.chooseAmongTerminalLinks(currentNode, model.getTokensNetwork()), currentNode, sentence, SupportedLanguages.IT);
                    break;
                }
        }
        return sentence.toString();
    }

}
