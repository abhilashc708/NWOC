package com.nwoc.a3gs.group.app.repository;


import com.nwoc.a3gs.group.app.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {

}
