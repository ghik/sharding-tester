package com.avsystem.sharding;

import com.google.common.base.Throwables;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Countries {
    private static List<String> countries;

    public static List<String> getCountries() {
        return countries;
    }

    static {
        countries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        Names.class.getResourceAsStream("/countries.txt")
                ))) {
            String line;
            while ((line = br.readLine()) != null) {
                countries.add(line);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
