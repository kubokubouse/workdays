package com.example.demo.service;

import com.example.demo.model.RegularTime;
import com.example.demo.repository.RegularTimeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegularTimeService {
    @Autowired
    RegularTimeRepository rtRepository;

    public RegularTime findId(int id){
        return rtRepository.findById(id);
    }

    public List<RegularTime> findAll(){
        return rtRepository.findAll();
    }
    public void update(RegularTime regularTime){
        rtRepository.save(regularTime);
    }
    public void insert(RegularTime regularTime){
        rtRepository.save(regularTime);
    }
    public void delete(int id){
        RegularTime regularTime=findId(id);
        rtRepository.delete(regularTime);
    }
}
