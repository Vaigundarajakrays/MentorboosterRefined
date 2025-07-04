package com.mentorboosters.app.controller;

import java.util.*;
import java.util.stream.*;

public class Hel {
    public static void main(String[] args) {

        List<List<String>> nestedList = Arrays.asList(
                Arrays.asList("apple", "mango"),
                Arrays.asList("orange", "pineapple"),
                Arrays.asList("grapes", "kiwi")
        );

        System.out.println("NESTED LIST OUTPUT: " + nestedList);

        List<String> flattenedUpperCaseList = nestedList.stream()
                .peek(innerList -> System.out.println("🔹 Original inner list: " + innerList)) // stream<List<String>>

                .flatMap(list -> {
                    System.out.println("🔸 Streaming list: " + list);
                    return list.stream();
                }) // stream<String>

                .peek(value -> System.out.println("🔹 After flatMap: " + value))

                .map(str -> {
                    String upper = str.toUpperCase();
                    System.out.println("🔸 Mapping to upper: " + str + " -> " + upper);
                    return upper;
                })

                .peek(upper -> System.out.println("🔹 After map: " + upper))

                .collect(Collectors.toList());

        System.out.println("✅ Final List: " + flattenedUpperCaseList);
    }
}

