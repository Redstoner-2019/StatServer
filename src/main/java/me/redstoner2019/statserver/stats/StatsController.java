package me.redstoner2019.statserver.stats;

import me.redstoner2019.statserver.stats.challenge.Challenge;
import me.redstoner2019.statserver.stats.challenge.entry.ChallengeEntry;
import me.redstoner2019.statserver.stats.challenge.entry.ChallengeEntryJpaRepository;
import me.redstoner2019.statserver.stats.challenge.ChallengeJpaRepository;
import me.redstoner2019.statserver.stats.challenge.version.ChallengeVersion;
import me.redstoner2019.statserver.stats.challenge.version.ChallengeVersionJpaRepository;
import me.redstoner2019.statserver.stats.game.Game;
import me.redstoner2019.statserver.stats.game.GameJpaRepository;
import me.redstoner2019.statserver.stats.util.AuthenticationHelper;
import me.redstoner2019.statserver.stats.util.AuthenticationResult;
import me.redstoner2019.statserver.stats.version.Version;
import me.redstoner2019.statserver.stats.version.VersionJpaRepository;
import me.redstoner2019.util.http.Requests;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class StatsController {
    public final GameJpaRepository gameJpaRepository;
    public final ChallengeJpaRepository challengeJpaRepository;
    public final ChallengeEntryJpaRepository challengeEntryJpaRepository;
    public final VersionJpaRepository versionJpaRepository;
    public final ChallengeVersionJpaRepository challengeVersionJpaRepository;
    private final Logger logger = Logger.getLogger(StatsController.class.getName());

    public StatsController(GameJpaRepository gameJpaRepository, ChallengeJpaRepository challengeJpaRepository, ChallengeEntryJpaRepository challengeEntryJpaRepository, VersionJpaRepository versionJpaRepository, ChallengeVersionJpaRepository challengeVersionJpaRepository) {
        this.gameJpaRepository = gameJpaRepository;
        this.challengeJpaRepository = challengeJpaRepository;
        this.challengeEntryJpaRepository = challengeEntryJpaRepository;
        this.versionJpaRepository = versionJpaRepository;
        this.challengeVersionJpaRepository = challengeVersionJpaRepository;

        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
                logger.log(Level.INFO,"Init done");
            }
        }).start();
    }

    public void init(){
        gameJpaRepository.deleteAll();
        challengeJpaRepository.deleteAll();
        challengeEntryJpaRepository.deleteAll();
        versionJpaRepository.deleteAll();
        challengeVersionJpaRepository.deleteAll();

        String username = "Redstoner_2019";

        Game game = new Game("FNaF", username);

        gameJpaRepository.save(game);

        JSONObject data = new JSONObject();

        data.put("powerLeft",0.1);
        data.put("death","foxy");

        Challenge ventablack = new Challenge("Ventablack","Ventablack test",game.getId(),username,data);
        Challenge night6 = new Challenge("Night 6","Night 6 test",game.getId(),username,data);

        challengeJpaRepository.save(ventablack);
        challengeJpaRepository.save(night6);

        versionJpaRepository.save(new Version(game.getId(),"1.0.0",0));
        versionJpaRepository.save(new Version(game.getId(),"1.1.0",1));
        versionJpaRepository.save(new Version(game.getId(),"1.2.0",2));
        versionJpaRepository.save(new Version(game.getId(),"1.3.0",3));

        challengeVersionJpaRepository.save(new ChallengeVersion("1.0.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.1.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.2.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.3.0",ventablack.getId()));

        challengeVersionJpaRepository.save(new ChallengeVersion("1.2.0",night6.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.3.0",night6.getId()));

        for (int i = 0; i < 50; i++) {
            data.put("powerLeft",Math.random());

            ChallengeEntry entry = new ChallengeEntry(ventablack.getId(),game.getId(),"1.3.0",data,username, data.getDouble("powerLeft"));
            challengeEntryJpaRepository.save(entry);
        }
    }

    @PostMapping("/stats/game/create")
    public ResponseEntity<String> createGame(@RequestBody String json, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(json);

            if(!request.has("name")){
                return getError(400,"Missing Name");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            String name = request.getString("name");

            Game game = gameJpaRepository.findByName(name);

            if(game != null){
                return getError(400,"This game already exists");
            }

            game = new Game(name, username);
            gameJpaRepository.save(game);

            JSONObject response = new JSONObject();
            response.put("result", "created");
            response.put("game", game.toJSON());

            return ResponseEntity.ok(response.toString());
        }catch (Exception e){
            return getError(400,e.getLocalizedMessage());
        }
    }

    @PostMapping("/stats/challenge/create")
    public ResponseEntity<String> createChallenge(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("name")){
                return getError(400,"Missing name");
            }

            if(!request.has("description")){
                return getError(400,"Missing description");
            }

            if(!request.has("game")){
                return getError(400,"Missing game");
            }

            if(!request.has("data")){
                return getError(400,"Missing example data");
            }

            if(!request.has("versions")){
                return getError(400,"Missing versions");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            String name = request.getString("name");
            String game = request.getString("game");
            JSONObject exampleData = request.getJSONObject("data");

            Challenge challenge = challengeJpaRepository.findByNameAndGame(name,game);

            if(challenge != null){
                return ResponseEntity.ok(challenge.toJSON().toString());
            }

            challenge = new Challenge(name, request.getString("description"), game, username, exampleData);

            challengeJpaRepository.save(challenge);

            JSONArray versions = request.getJSONArray("versions");
            for (int i = 0; i < versions.length(); i++) {
                String version = versions.getString(i);

                ChallengeVersion challengeVersion = new ChallengeVersion(version, challenge.getId());

                challengeVersionJpaRepository.save(challengeVersion);
            }

            return ResponseEntity.ok(challenge.toJSON().toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/stats/version/create")
    public ResponseEntity<String> createVersion(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("game")){
                return getError(400,"Missing game");
            }

            if(!request.has("version")){
                return getError(400,"Missing version");
            }

            if(!request.has("versionNumber")){
                return getError(400,"Missing versionNumber");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            Optional<Version> versionOpt = versionJpaRepository.findVersionByGameAndVersionNumber(request.getString("game"), request.getLong("versionNumber"));

            if(versionOpt.isPresent()){
                return ResponseEntity.status(210).body(versionOpt.get().toJSON().toString());
            }

            versionOpt = versionJpaRepository.findVersionByGameAndVersion(request.getString("game"), request.getString("version"));

            if(versionOpt.isPresent()){
                return ResponseEntity.status(210).body(versionOpt.get().toJSON().toString());
            }

            Version version = new Version(request.getString("game"), request.getString("version"), request.getLong("versionNumber"));

            versionJpaRepository.save(version);

            return ResponseEntity.ok(version.toJSON().toString());
        }catch (Exception e){
            return getError(400,e.getMessage());
        }
    }

    @PostMapping("/stats/challengeEntry/create")
    public ResponseEntity<String> createChallengeEntry(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("challengeId")){
                return getError(400,"Missing challengeId");
            }

            if(!request.has("data")){
                return getError(400,"Missing data");
            }

            if(!request.has("game")){
                return getError(400,"Missing game");
            }

            if(!request.has("version")){
                return getError(400,"Missing version");
            }

            if(!request.has("score")){
                return getError(400,"Missing score");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            JSONObject data = request.getJSONObject("data");

            Optional<Challenge> challengeOpt = challengeJpaRepository.findById(request.getString("challengeId"));

            if(challengeOpt.isEmpty()){
                return getError(400,"This challenge does not exist");
            }

            Challenge challenge = challengeOpt.get();

            JSONObject exampleData = challenge.getData();

            for(String key : exampleData.keySet()){
                if(!data.has(key)){
                    return getError(400,"Required key not found: " + key);
                }
                if(!data.get(key).getClass().equals(exampleData.get(key).getClass())){
                    return getError(400,"Required key types do not match for " + key + ". Required: " + exampleData.get(key).getClass() + " found " + data.get(key).getClass());
                }
            }

            for(String key : data.keySet()){
                if(!exampleData.has(key)){
                    return getError(400,"Key not accepted: " + key);
                }
            }

            ChallengeEntry entry = new ChallengeEntry(challenge.getId(),request.getString("game"),request.getString("version"), data, username, request.getDouble("score"));

            challengeEntryJpaRepository.save(entry);

            return ResponseEntity.ok(request.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Getting
     */

    @GetMapping("/stats/game/getAll")
    public ResponseEntity<String> getAllGames(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            JSONArray games = new JSONArray();

            logger.log(Level.INFO,"All Games");

            List<Game> gameList = gameJpaRepository.findAll();

            for(Game game : gameList){
                JSONObject g = game.toJSON();
                JSONArray versions = new JSONArray();

                for(Version version : versionJpaRepository.findAllByGame(game.getId())){
                    versions.put(version.getId());
                }

                g.put("versions",versions);
                games.put(g);
            }
            return ResponseEntity.ok(games.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/stats/versions/getAll")
    public ResponseEntity<String> getAllVersions(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("game")){
                return getError(400,"Missing game");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            String game = request.getString("game");

            JSONArray versionsArray = new JSONArray();

            List<Version> versions = versionJpaRepository.findAllByGame(game);

            for(Version version : versions){
                versionsArray.put(version.toJSON());
            }

            return ResponseEntity.ok(versionsArray.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/stats/challenges/getAll")
    public ResponseEntity<String> getAllChallenges(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("game")){
                return getError(400,"Missing game");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            String game = request.getString("game");

            String version = null;

            if(request.has("version")){
                version = request.getString("version");
            }

            JSONArray challengesArray = new JSONArray();

            List<Challenge> challenges = challengeJpaRepository.findByGame(game);

            for(Challenge c : challenges){
                if(version != null) {
                    Optional<ChallengeVersion> ocv = challengeVersionJpaRepository.findByVersionAndChallenge(version, c.getId());
                    if(ocv.isEmpty()) continue;
                }
                challengesArray.put(c.toJSON());
            }

            return ResponseEntity.ok(challengesArray.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/stats/challengeEntry/getAll")
    public ResponseEntity<String> getAllChallengeEntries(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("pageSize")){
                return getError(400,"Missing pageSize");
            }

            if(!request.has("pageNumber")){
                return getError(400,"Missing pageNumber");
            }

            if(!request.has("challenge")){
                return getError(400,"Missing challenge");
            }

            if(!request.has("game")){
                return getError(400,"Missing game");
            }

            if(!request.has("version")){
                return getError(400,"Missing version");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            int pageSize = request.getInt("pageSize");
            int pageNumber = request.getInt("pageNumber");
            String challenge = request.getString("challenge");
            String game = request.getString("game");
            String version = request.getString("version");

            JSONArray challengeEntries = new JSONArray();

            List<ChallengeEntry> challengeEntryList = new ArrayList<>(getChallengeEntries(challenge,game,version,pageNumber,pageSize).toList());
            challengeEntryList.sort(Comparator.comparingDouble(ChallengeEntry::getScore).thenComparing(ChallengeEntry::getCreated).reversed());
            for(ChallengeEntry challengeEntry : challengeEntryList){
                challengeEntries.put(challengeEntry.toJSON());
            }
            return ResponseEntity.ok(challengeEntries.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @GetMapping("/stats/challengeEntry/getAll/pages")
    public ResponseEntity<String> getAllChallengeEntriesPages(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("pageSize")){
                return getError(400,"Missing pageSize");
            }

            if(!request.has("challenge")){
                return getError(400,"Missing pageNumber");
            }

            if(!request.has("game")){
                return getError(400,"Missing game");
            }

            if(!request.has("version")){
                return getError(400,"Missing version");
            }

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            int pageSize = request.getInt("pageSize");
            String challenge = request.getString("challenge");
            String game = request.getString("game");
            String version = request.getString("version");

            JSONObject result = new JSONObject();

            result.put("pages",getChallengeEntriesPages(challenge,game, version, pageSize));

            return ResponseEntity.ok(result.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }








    @PostMapping("/stats/game/get")
    public ResponseEntity<String> getGame(@RequestBody String s) {
        try{
            JSONObject request = new JSONObject(s);

            if (request.has("uuid")){
                Optional<Game> gameOpt = gameJpaRepository.findById(request.getString("uuid"));
                return gameOpt.map(game -> ResponseEntity.ok(game.toJSON().toString())).orElseGet(() -> getError(404, "uuid not found"));
            } else {
                return getError(400,"Missing UUID, or invalid game description");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<String> getError(int status, String message){
        JSONObject response = new JSONObject();
        response.put("error",message);
        return ResponseEntity.status(status).body(response.toString());
    }

    public Page<ChallengeEntry> getChallengeEntries(String challengeId,String game, String version, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return challengeEntryJpaRepository.findByChallengeIdAndGameAndVersionOrderByScore(challengeId,game,version, pageable);
    }

    public int getChallengeEntriesPages(String challengeId,String game, String version, int size) {
        PageRequest pageable = PageRequest.of(0, size);
        return challengeEntryJpaRepository.findByChallengeIdAndGameAndVersionOrderByScore(challengeId,game,version, pageable).getTotalPages();
    }
}
