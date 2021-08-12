package com.gsc.reater.service.gen;

import com.gsc.reater.model.ReaterModel;

import java.util.Optional;

public interface FileInputOutputService {

    Optional<ReaterModel> readModelFile(String path);

    void saveModelFile(String path, ReaterModel model);
}
