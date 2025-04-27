package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private final Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getDeveloperById(@PathVariable int id) {
        Developer developer = developers.get(id);
        if (developer != null) {
            return ResponseEntity.ok(developer);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Developer> addDeveloper(@RequestBody Developer developer) {
        Developer newDeveloper;
        double netSalary;

        switch (developer.getExperience()) {
            case JUNIOR:
                netSalary = developer.getSalary() * (1 - taxable.getSimpleTaxRate() / 100);
                newDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(), netSalary);
                break;
            case MID:
                netSalary = developer.getSalary() * (1 - taxable.getMiddleTaxRate() / 100);
                newDeveloper = new MidDeveloper(developer.getId(), developer.getName(), netSalary);
                break;
            case SENIOR:
                netSalary = developer.getSalary() * (1 - taxable.getUpperTaxRate() / 100);
                newDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(), netSalary);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        developers.put(newDeveloper.getId(), newDeveloper);
        return new ResponseEntity<>(newDeveloper, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> updateDeveloper(@PathVariable int id, @RequestBody Developer developer) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        developers.put(id, developer);
        return ResponseEntity.ok(developer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable int id) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }

        developers.remove(id);
        return ResponseEntity.ok().build();
    }
}