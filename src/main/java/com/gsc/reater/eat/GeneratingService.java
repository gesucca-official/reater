package com.gsc.reater.eat;

import java.util.List;

public interface GeneratingService {

    List<String> generateSentences(String pathToModelFile, int numberOfSentences);
}
