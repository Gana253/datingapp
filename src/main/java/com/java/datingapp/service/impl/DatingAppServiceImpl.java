package com.java.datingapp.service.impl;

import com.java.datingapp.model.User;
import com.java.datingapp.service.DatingAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("datingAppService")
public class DatingAppServiceImpl implements DatingAppService {
    public static final String TAB = "\\t";

    private final Logger log = LoggerFactory.getLogger(DatingAppServiceImpl.class);

    @Autowired
    private ResourceLoader resourceLoader;


    @Value("${matchinguser.count}")
    private int matchingUserCount; //count to fetch number for matching users

    @Value("${inputfile.name}")
    private String filePath; //Tsv file input path

    private List<User> userList;

    @PostConstruct
    public void init() {

        //Read all the users from the TSV file on server startup
        userList = retrieveUserDtls();
    }

    /**
     * Method to read the TSV file and create user list
     *
     * @return
     */
    private List<User> retrieveUserDtls() {

        List<User> userLst = new ArrayList<>();

        Resource userResource = resourceLoader.getResource(filePath);

        try {
            File file = userResource.getFile();
            List<String> ratingsData = Files.readAllLines(file.toPath());

            try (Stream<String> stream = ratingsData.stream()) {

                userLst = stream.skip(1).map(line -> line.split(TAB)).map(data -> {
                    User user = new User();
                    for (int i = 0; i < data.length; i++) {
                        switch (i) {
                            case 0:
                                user.setName(data[i]);
                                break;

                            case 1:
                                user.setGender(data[i]);
                                break;
                            case 2:
                                user.setAge(Integer.parseInt(data[i]));
                                break;
                            default:
                                user.setInterests(Arrays.asList(data[i].split(",")));
                                break;
                        }
                    }
                    return user;
                }).collect(Collectors.toList());
            }

        } catch (IOException e) {
            log.error("Error Occurred -> {}", e.getMessage());
        }

        return userLst;

    }

    /**
     * Method to retrieve the Matching User for the given username
     *
     * @param userName
     * @return
     */
    @Override
    public List<String> retrieveMatchingUser(String userName) {
        //Get the user object(Given UserName for which the matches to be found) from the List.
        Optional<User> matchingObject = userList.stream().
                filter(p -> p.getName().equals(userName)).
                findFirst();
        //If the user is not found then throw RunTimeException
        User givenUser = matchingObject.orElseThrow(() -> new RuntimeException("User Not Found"));
        Map<Double, List<User>> userMapWithWeighing = new HashMap<>();
        //Based on the interest,age and gender calculate the weighing and store it in map
        findTheWeightageForEachUser(givenUser, userMapWithWeighing);
        //Sort the map using the key in Ascending order
        Map<Double, List<User>> sortedMatchingUserMap = userMapWithWeighing.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        //Return the list of matching users to fetch all the matching users then configure the matchinguser.count = 0 in application.properties
        return sortedMatchingUserMap.entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                        .map(User::getName)
                ).limit(matchingUserCount != 0 ? matchingUserCount : sortedMatchingUserMap.size()).collect(Collectors.toList());


    }

    /**
     * Find the weight for each user based on age,interest and gender and store it in map
     *
     * @param givenUser
     * @param userMapWithWeighing
     */
    private void findTheWeightageForEachUser(User givenUser, Map<Double, List<User>> userMapWithWeighing) {
        for (User usr : userList) {
            if (usr.getName().equals(givenUser.getName())) continue;
            //To find out matching interest between users
            int interestMatchCount = (int) givenUser.getInterests().stream().filter(item -> !givenUser.getInterests().contains(item)).count();
            double age = Math.abs(givenUser.getAge() - usr.getAge()) * userList.size();
            double gender = givenUser.getGender().equals(usr.getGender()) ? 2000 : 1000;
            double finalWeight = interestMatchCount + age + gender;
            updateUserMap(userMapWithWeighing, usr, interestMatchCount, age, gender, finalWeight);
        }
    }

    /**
     * Update the usermap based on the calculated weightage.
     *
     * @param userMapWithWeighing
     * @param usr
     * @param matchCount
     * @param age
     * @param gender
     * @param finalWeight
     */
    private void updateUserMap(Map<Double, List<User>> userMapWithWeighing, User usr, int matchCount, double age, double gender, double finalWeight) {
        if (null != userMapWithWeighing.get(finalWeight)) {
            userMapWithWeighing.get(matchCount + age + gender).add(usr);
        } else {
            List<User> uarLastBasedOnWeight = new ArrayList<>();
            uarLastBasedOnWeight.add(usr);
            userMapWithWeighing.put(finalWeight, uarLastBasedOnWeight);
        }
    }
}
