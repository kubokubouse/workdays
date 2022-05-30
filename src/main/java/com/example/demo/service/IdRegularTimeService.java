package com.example.demo.service;

import com.example.demo.model.BeanRegularTime;
import com.example.demo.model.Idregulartime;
import com.example.demo.repository.IdRegularTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdRegularTimeService {
    @Autowired
    IdRegularTimeRepository rtRepository;

    public Idregulartime findId(int id){
        return rtRepository.findByUserid(id);
    }

    
    public void update(Idregulartime idregularTime){
        rtRepository.save(idregularTime);
    }
    public void insert(Idregulartime idregularTime){
        rtRepository.save(idregularTime);
    }
    public void delete(int userid){
        Idregulartime idregularTime=findId(userid);
        rtRepository.delete(idregularTime);
    }
    public BeanRegularTime brtime(Idregulartime idtime){
        BeanRegularTime brtime=new BeanRegularTime();
        brtime.setStart(idtime.getStart());
        brtime.setEnd(idtime.getEnd());
        brtime.setHalftime(idtime.getHalftime());
        
        return brtime;
    }
}
