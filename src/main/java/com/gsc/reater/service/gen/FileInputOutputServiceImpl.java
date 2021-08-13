package com.gsc.reater.service.gen;

import com.gsc.reater.model.ReaterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileInputOutputServiceImpl implements FileInputOutputService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String NO_INPUT_FILE_CONTENT = "NO CONTENT";

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

    @Override
    public String readRawTextFile(String path) {
        String content = FileInputOutputServiceImpl.NO_INPUT_FILE_CONTENT;
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(path));
            content = reader.lines().collect(Collectors.joining(" "));
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (content.equals(FileInputOutputServiceImpl.NO_INPUT_FILE_CONTENT))
            throw new InputMismatchException("Invalid or Empty Input File");
        return content;
    }
}
