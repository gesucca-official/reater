package com.gsc.reater.service.shit;

import java.util.List;

public interface ShittingService {

    List<String> generateSentences(String pathToModelFile, int numberOfSentences);
}
