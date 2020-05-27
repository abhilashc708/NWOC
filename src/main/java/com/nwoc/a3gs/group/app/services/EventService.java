package com.nwoc.a3gs.group.app.services;

import com.nwoc.a3gs.group.app.dto.EventDto;
import com.nwoc.a3gs.group.app.model.Event;
import com.nwoc.a3gs.group.app.repository.EventRepository;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class EventService {

    private static final Logger LOGGER = LogManager.getLogger(EventService.class);
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EventRepository eventRepository;


    @Transactional
    public boolean save(EventDto evntDto) throws ParseException {
        boolean isSave = false;
        Event event= new Event();
        //Date dateDOB=new SimpleDateFormat("dd-MM-yyyy").parse(candidatesDTO.getDob());
        BeanUtils.copyProperties(evntDto, event);
        //event.setDob(dateDOB);
        MultipartFile file = evntDto.getFiles();
        if (file != null) {
            String filePath = fileStorageService.storeFileInAPath(file);
            event.setImage(filePath);
            evntDto.setFiles(null);
            eventRepository.save(event);
            isSave = true;
            LOGGER.info("Candidate  Creation Successfully");
        }
        else
        {
            LOGGER.error("Candidates creation is failed");
            return isSave;
        }
        return isSave;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> findOne(Long candidate_id) {
        return eventRepository.findById(candidate_id);
    }

    public boolean update(EventDto evntDto, Long eventId) throws NotFoundException, ParseException {
        boolean isUpdated=false;

        Optional<Event> eventOpt= findOne(eventId);
        if(!eventOpt.isPresent()){
            throw new NotFoundException("Candidate not found");
        }
        Event event= eventOpt.get();
        BeanUtils.copyProperties(evntDto, event);
        MultipartFile file = evntDto.getFiles();
        if (file != null) {
            String filePath = fileStorageService.storeFileInAPath(file);
            event.setImage(filePath);
            evntDto.setFiles(null);
        }
        MultipartFile bannerfile = evntDto.getBannerFiles();
        if (file != null) {
            String filePath = fileStorageService.storeFileInAPath(file);
            event.setBannerImage(filePath);
            evntDto.setFiles(null);
        }
        eventRepository.save(event);
        isUpdated=true;
        return isUpdated;
    }

    public void delete(Event event) {
        eventRepository.delete(event);
    }

    public Page<Event> findEventsByPages(int pageNumber, int size) {
        Pageable pageable = new PageRequest(pageNumber, size);
        return eventRepository.findAll(pageable);
    }
}
