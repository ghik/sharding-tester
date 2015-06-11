package com.avsystem.sharding;

import com.google.common.base.Throwables;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Names {
    private static List<String> names;

    public static List<String> getNames() {
        return names;
    }

    static {
        names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Names.class.getResourceAsStream("/imiona.txt")
                ))) {
            String line;
            while ((line = br.readLine()) != null) {
                names.add(line);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
