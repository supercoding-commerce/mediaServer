package com.github.mediaserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mediaserver.dto.ConnectionPropertiesDto;
import com.github.mediaserver.dto.PostConnectionDto;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "*")
@RestController
public class VideoController {
    private OpenVidu openVidu;

    @Value("${spring.openvidu.secret}")
    private String openviduSecret;

    @PostConstruct
    public void init() {
        this.openVidu = new OpenVidu("https://openvidu:4443", openviduSecret);
    }


    /**
     * @param params The Session properties
     * @return The Session ID
     */
    @PostMapping("/api/sessions")
    public ResponseEntity<String> initializeSession(@RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        SessionProperties properties = SessionProperties.fromJson(params).build();
        Session session = openVidu.createSession(properties);
        return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
    }

    /**
     * @param sessionId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token associated to the Connection
     */
    @PostMapping("/api/{sessionId}/connections")
    public ResponseEntity<String> createConnection(
            @PathVariable("sessionId") String sessionId,
            @RequestBody(required = false) ConnectionPropertiesDto params)
            throws OpenViduJavaClientException, OpenViduHttpException, JsonProcessingException {
        Session session = openVidu.getActiveSession(sessionId);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String role = params.getRole();
        String type = params.getType();
        Boolean onlyPlayWithSubscribers = params.getOnlyPlayWithSubscribers();

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("role", role);
        paramsMap.put("type", type);
        paramsMap.put("onlyPlayWithSubscribers", onlyPlayWithSubscribers);

        ConnectionProperties properties = ConnectionProperties.fromJson(paramsMap).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }
}
