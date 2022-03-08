package com.example.demo.service;

import com.example.demo.repository.OnetimeRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Onetime;


@Service
public class OnetimeService {
    @Autowired
    OnetimeRepository onetimeRepository;
    
    public void insert(Onetime onetime){
        onetimeRepository.save(onetime);

    }

    public void delete(Onetime onetime){
        onetimeRepository.delete(onetime);

    }

    public List<Onetime> searchAll(){
        return onetimeRepository.findAll();
    }
    
    public Onetime findId(int id){
        return onetimeRepository.findById(id);

    }
    
}
