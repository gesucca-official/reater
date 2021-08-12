package com.gsc.reater.service.gen;

import com.gsc.reater.model.ReaterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
public class FileInputOutputServiceImpl implements FileInputOutputService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Optional<ReaterModel> readModelFile(String path) {
        log.info("Reading Model File at: " + path);
        try {
            FileInputStream fi = new FileInputStream(path);
            ObjectInputStream oi = new ObjectInputStream(fi);
            ReaterModel model = (ReaterModel) oi.readObject();
            oi.close();
            fi.close();
            log.info("Model File successfully imported.");
            return Optional.of(model);
        } catch (IOException | ClassNotFoundException e) {
            log.info("Something has broken!");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void saveModelFile(String path, ReaterModel model) {
        log.info("Writing Model to File at: " + path);
        try {
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(model);
            o.close();
            f.close();
            log.info("Model File successfully written.");
        } catch (IOException e) {
            log.info("Something has broken!");
            e.printStackTrace();
        }
    }
}
