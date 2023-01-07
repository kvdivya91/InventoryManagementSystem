package com.cleartrip.inventory.controller;

import com.cleartrip.inventory.exception.ResourceNotFoundException;
import com.cleartrip.inventory.model.Inventory;
import com.cleartrip.inventory.model.Item;
import com.cleartrip.inventory.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {
    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/items")
    public ResponseEntity<List<Item>> getReport() {
        List<Item> items = itemRepository.findAll();
        if(items.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Collections.sort(items, Comparator.comparingDouble(Item::getCost).reversed());
        return new ResponseEntity(items, HttpStatus.OK);
    }

    @GetMapping("items/{id}")
    public ResponseEntity<Item> getItem(@PathVariable("id") long id) throws ResourceNotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found for id :: " + id));
        return new ResponseEntity(item, HttpStatus.OK);
    }

    @PostMapping("/items/batch")
    public ResponseEntity<List<Item>> createInventories(@RequestBody Inventory<Item> inventory) {
        List<Item> list = inventory.getList();
        itemRepository.saveAll(list);
        return new ResponseEntity(list, HttpStatus.CREATED);
    }

    @PostMapping("/items")
    public ResponseEntity<Item> createInventory(@RequestBody Item item) {
        itemRepository.save(item);
        return new ResponseEntity(item, HttpStatus.CREATED);
    }

    @DeleteMapping("/items")
    public ResponseEntity<Item> deleteInventories()  {
        itemRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Item> deleteInventory(@PathVariable("id") long id) throws ResourceNotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Item not found for id :: " + id));
        itemRepository.deleteById(id);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @PutMapping("/items/batch")
    public ResponseEntity<List<Item>> updateInventories(@RequestBody Inventory<Item> inventory) {
        List<Item> list = inventory.getList();
        List<Item> result = new LinkedList<>();
        for(Item listItem : list) {
            Optional<Item> item = itemRepository.findById(listItem.getId());
            if(item.isPresent()) {
                item.get().setName(listItem.getName());
                item.get().setCost(listItem.getCost());
                item.get().setStock(listItem.getStock());
                result.add(item.get());
            }
        }
        itemRepository.saveAll(result);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<Item> updateInventory(@PathVariable("id") long id, @RequestBody Item item) throws ResourceNotFoundException {
        Item itemFromDb = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found for id :: " + id));
        itemFromDb.setName(item.getName());
        itemFromDb.setCost(item.getCost());
        itemFromDb.setStock(item.getStock());
        itemRepository.save(itemFromDb);
        return new ResponseEntity(itemFromDb, HttpStatus.OK);
    }



}
