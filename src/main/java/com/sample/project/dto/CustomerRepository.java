package com.sample.project.dto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sample.project.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	// Find the oldest receivedDate
	@Query("SELECT min(c.receivedDate) FROM Customer c")
	String findOldestReceivedDate();

	// Delete records older than a specific receivedDate
	@Modifying
	@Query("DELETE FROM Customer c WHERE c.receivedDate = ?1")
	void deleteByReceivedDate(String receivedDate);

	@Query("SELECT DISTINCT c.receivedDate FROM Customer c ORDER BY c.receivedDate ASC")
	List<String> findDistinctReceivedDates();

}
