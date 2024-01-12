package com.lognex.productrest.abstracttest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class AbstractProductRestTest {
    protected String readFileAsString(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while(line != null) {
                sb.append(line).append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }
}
