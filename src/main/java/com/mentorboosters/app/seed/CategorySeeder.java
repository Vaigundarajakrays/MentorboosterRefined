package com.mentorboosters.app.seed;

import com.mentorboosters.app.model.CategoryNew;
import com.mentorboosters.app.repository.CategoryNewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategorySeeder implements CommandLineRunner {


    private final CategoryNewRepository categoryNewRepository;

    @Override
    public void run(String... args) throws Exception {

        List<String> defaultCategories = List.of("Entrepreneurship", "Marketing", "Finance", "Product Development");

        for (String name : defaultCategories) {
            categoryNewRepository.findByName(name).orElseGet(() ->
                    categoryNewRepository.save(new CategoryNew(null, name))
            );
        }
        log.info("✅ Categories seeded successfully");
    }
}
