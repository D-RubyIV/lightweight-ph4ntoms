package com.ph4ntoms.authenticate.controller;

import com.ph4ntoms.authenticate.request.auth.ActivateRequest;
import com.ph4ntoms.authenticate.request.auth.SignInRequest;
import com.ph4ntoms.authenticate.request.auth.SignUpRequest;
import com.ph4ntoms.authenticate.service.authenticate.AuthenticationService;
import com.ph4ntoms.authenticate.service.entity.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/authenticate")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping(value = "sign-in")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @PostMapping(value = "sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @PostMapping(value = "activate")
    public ResponseEntity<?> activate(@RequestBody @Valid ActivateRequest request) {
        return ResponseEntity.ok(authenticationService.activate(request));
    }

    @GetMapping(value = "validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authenticationService.isValidToken(token));
    }
}
