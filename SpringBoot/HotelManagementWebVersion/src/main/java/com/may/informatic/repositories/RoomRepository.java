package com.may.informatic.repositories;

import com.may.informatic.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("SELECT r FROM Room r WHERE r.name LIKE %:searchQuery%")
    Page<Room> findByNameContaining(String searchQuery, Pageable pageable);
}