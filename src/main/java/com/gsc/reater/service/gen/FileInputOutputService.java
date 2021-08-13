package com.gsc.reater.service.gen;

import com.gsc.reater.model.ReaterModel;

import java.util.Optional;

public interface FileInputOutputService {

    String readRawTextFile(String path);

    Optional<ReaterModel> readModelFile(String path);

    void saveModelFile(String path, ReaterModel model);
}
