package com.java.datingapp.controller;

import com.java.datingapp.service.DatingAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/datingapp")
public class DatingAppController {
    private final Logger log = LoggerFactory.getLogger(DatingAppController.class);
    @Autowired
    private DatingAppService datingAppService;

    /**
     * Search for the matching user for the given user.
     *
     * @param userName - user for which the match should be fetched
     * @return
     */
    @GetMapping(path = "/searchMatch/{userName}", produces = {MediaType.APPLICATION_JSON_VALUE})
    private ResponseEntity<?> searchMatchingUserForGivenUser(@PathVariable String userName) {
        log.info("Request Received for fetching match for the user--{}", userName);
        try {
            return new ResponseEntity<>(datingAppService.retrieveMatchingUser(userName), HttpStatus.OK);
        } catch (RuntimeException re) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("User Not Found", "Invalid User Selected");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers)
                    .body("Error occurred - Couldn't find the given user in database");
        }
    }

}
