package com.demo.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.api.models.FileInfo;

@Repository
public interface FileRepository extends JpaRepository<FileInfo, String> {

}
