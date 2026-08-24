# MAMA MPOKI CAR HIRE — Authentication & Authorization Flow

## 1. Overview

Since this is an **owner-only system**, authentication is straightforward:
- Single user (the owner) logs in with username/password
- JWT access token (24h) + refresh token (7 days) are issued
- No role-based access control needed in v1 (owner has full access)
- Rate limiting protects against brute force on login

## 2. JWT Authentication Flow

```
┌──────────┐                    ┌──────────────────┐                    ┌──────────┐
│  Owner   │                    │  Spring Boot     │                    │  MySQL   │
│ (Client) │                    │  Backend         │                    │ Database │
└────┬─────┘                    └────────┬─────────┘                    └────┬─────┘
     │                                   │                                   │
     │  1. POST /api/v1/auth/login       │                                   │
     │  { username, password }           │                                   │
     │──────────────────────────────────►│                                   │
     │                                   │  2. Check rate limit              │
     │                                   │     (5 attempts/min per IP)       │
     │                                   │                                   │
     │                                   │  3. Query owner by username       │
     │                                   │──────────────────────────────────►│
     │                                   │                                   │
     │                                   │  4. Return owner record           │
     │                                   │◄──────────────────────────────────│
     │                                   │                                   │
     │                                   │  5. Verify password (BCrypt)      │
     │                                   │                                   │
     │  6. Return access + refresh tokens│                                   │
     │◄──────────────────────────────────│                                   │
     │                                   │                                   │
     │  7. Include access token in header│                                   │
     │  Authorization: Bearer <token>    │                                   │
     │──────────────────────────────────►│                                   │
     │                                   │  8. JwtAuthenticationFilter       │
     │                                   │     validates token               │
     │                                   │                                   │
     │  9. Return protected resource     │                                   │
     │◄──────────────────────────────────│                                   │
     │                                   │                                   │
     │  === When access token expires ===│                                   │
     │                                   │                                   │
     │  10. POST /api/v1/auth/refresh    │                                   │
     │  { refreshToken }                 │                                   │
     │──────────────────────────────────►│                                   │
     │                                   │  11. Validate refresh token       │
     │                                   │──────────────────────────────────►│
     │                                   │                                   │
     │  12. Return new access token      │                                   │
     │◄──────────────────────────────────│                                   │
```

## 3. JWT Token Structure

### Access Token (24 hours)
```json
{
  "sub": "1",                          // Owner ID
  "username": "mamampoki",             // Owner username
  "type": "access",                    // Token type
  "iat": 1724500000,                   // Issued at
  "exp": 1724586400                    // Expires (24 hours)
}
```

### Refresh Token (7 days)
```json
{
  "sub": "1",                          // Owner ID
  "type": "refresh",                   // Token type
  "iat": 1724500000,                   // Issued at
  "exp": 1725104800                    // Expires (7 days)
}
```

### Token Configuration
```yaml
# application.yml
app:
  jwt:
    secret: ${JWT_SECRET:mama-mpoki-secret-key-change-in-production}
    access-token-expiration: 86400000     # 24 hours
    refresh-token-expiration: 604800000   # 7 days
    header: Authorization
    prefix: "Bearer "
```

## 4. Security Implementation

### SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/auth/refresh").permitAll()
                .requestMatchers("/api/v1/auth/change-password").authenticated()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use environment variable for production URL
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",           // Dev
            "${FRONTEND_URL:http://localhost:3000}"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

### JwtTokenProvider.java
```java
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(Owner owner) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
            .setSubject(String.valueOf(owner.getId()))
            .claim("username", owner.getUsername())
            .claim("type", "access")
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public String generateRefreshToken(Owner owner) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
            .setSubject(String.valueOf(owner.getId()))
            .claim("type", "refresh")
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public Long getOwnerIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public String getTokenType(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        return claims.get("type", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }
}
```

### JwtAuthenticationFilter.java
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private OwnerDetailsService ownerDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)
                && tokenProvider.validateToken(token)
                && tokenProvider.isAccessToken(token)) {

            Long ownerId = tokenProvider.getOwnerIdFromToken(token);
            OwnerDetails ownerDetails = ownerDetailsService.loadUserById(ownerId);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    ownerDetails, null, ownerDetails.getAuthorities());

            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### RateLimitingFilter.java
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final LoadingCache<String, Integer> loginAttempts = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<>() {
            @Override
            public Integer load(String key) {
                return 0;
            }
        });

    private static final int MAX_ATTEMPTS = 5;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/v1/auth/login")
                && "POST".equals(request.getMethod())) {

            String clientIp = getClientIP(request);
            int attempts = loginAttempts.getUnchecked(clientIp);

            if (attempts >= MAX_ATTEMPTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"success\":false,\"message\":\"Too many login attempts. " +
                    "Please try again in 60 seconds.\",\"retryAfter\":60}");
                return;
            }

            loginAttempts.put(clientIp, attempts + 1);
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

## 5. Password Security

### Password Hashing
- Algorithm: **BCrypt** (Spring Security default)
- Strength: 12 rounds
- Stored as hash, never plain text

### Initial Setup
```sql
-- Insert owner with BCrypt hashed password
-- Password: "MamaMpoki2026!" (change immediately after first login)
INSERT INTO owner (username, password, full_name, phone, email)
VALUES ('mamampoki',
        '$2a$12$LJ3m4ys3Lz0wqV9rQ5kZYOeQZ8z9eN3xG2VH5yT8cR6bW4sK2mXy',
        'Mama Mpoki',
        '+255XXXXXXXXX',
        'info@mamampoki.co.tz');
```

### Change Password Flow
```json
POST /api/v1/auth/change-password
Authorization: Bearer <token>
{
  "currentPassword": "old_password",
  "newPassword": "new_secure_password"
}
```

### Password Validation Rules
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 number
- At least 1 special character

## 6. Frontend Integration

### Storing Tokens
```javascript
// After successful login
const login = async (username, password) => {
  const response = await axios.post('/api/v1/auth/login', {
    username,
    password
  });

  const { accessToken, refreshToken, owner } = response.data.data;

  // Store tokens
  localStorage.setItem('accessToken', accessToken);
  localStorage.setItem('refreshToken', refreshToken);
  localStorage.setItem('owner', JSON.stringify(owner));
};
```

### Axios Interceptor with Auto-Refresh
```javascript
let isRefreshing = false;
let failedQueue = [];

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

// Add token to all requests
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle 401 and auto-refresh
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return axios(originalRequest);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post('/api/v1/auth/refresh', {
          refreshToken
        });

        const { accessToken } = response.data.data;
        localStorage.setItem('accessToken', accessToken);

        axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
        processQueue(null, accessToken);

        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return axios(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('owner');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
```

## 7. Error Responses

### Invalid Credentials
```json
{
  "success": false,
  "message": "Invalid username or password"
}
```

### Rate Limited
```json
{
  "success": false,
  "message": "Too many login attempts. Please try again in 60 seconds.",
  "retryAfter": 60
}
```

### Expired Access Token
```json
{
  "success": false,
  "message": "Access token has expired"
}
```

### Invalid Refresh Token
```json
{
  "success": false,
  "message": "Invalid or expired refresh token"
}
```

### Missing Token
```json
{
  "success": false,
  "message": "Authorization token is missing"
}
```

## 8. Security Checklist

- [ ] BCrypt password hashing (strength 12)
- [ ] JWT access token (24h) + refresh token (7 days)
- [ ] Rate limiting on login (5 attempts/min per IP)
- [ ] CORS restricted to frontend URL
- [ ] Stateless sessions (no server-side session storage)
- [ ] HTTPS in production
- [ ] JWT secret from environment variable (not hardcoded)
- [ ] Refresh token stored securely (httpOnly cookie in production)
- [ ] Password change requires current password
