package me.redstoner2019.statserver.stats;

import me.redstoner2019.statserver.stats.challenge.Challenge;
import me.redstoner2019.statserver.stats.challenge.ChallengeEntry;
import me.redstoner2019.statserver.stats.challenge.ChallengeEntryJpaRepository;
import me.redstoner2019.statserver.stats.challenge.ChallengeJpaRepository;
import me.redstoner2019.statserver.stats.game.Game;
import me.redstoner2019.statserver.stats.game.GameJpaRepository;
import me.redstoner2019.statserver.stats.version.Version;
import me.redstoner2019.statserver.stats.version.VersionJpaRepository;
import me.redstoner2019.util.http.Requests;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
    private final Logger logger = Logger.getLogger(StatsController.class.getName());
    private final String authIP = "http://158.220.105.209:8080";

    public StatsController(GameJpaRepository gameJpaRepository, ChallengeJpaRepository challengeJpaRepository, ChallengeEntryJpaRepository challengeEntryJpaRepository, VersionJpaRepository versionJpaRepository) {
        this.gameJpaRepository = gameJpaRepository;
        this.challengeJpaRepository = challengeJpaRepository;
        this.challengeEntryJpaRepository = challengeEntryJpaRepository;
        this.versionJpaRepository = versionJpaRepository;

        init();
    }

    public void init(){
        gameJpaRepository.deleteAll();
        challengeJpaRepository.deleteAll();
        challengeEntryJpaRepository.deleteAll();
        versionJpaRepository.deleteAll();

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
    }

    @PostMapping("/stats/game/create")
    public ResponseEntity<String> createGame(@RequestBody String json, @RequestHeader HttpHeaders headers) {
        try{
            JSONObject request = new JSONObject(json);

            if(!request.has("name")){
                return getError(400,"Missing Name");
            }

            if(!request.has("token") && !headers.containsKey("token")){
                return getError(400,"Missing version number");
            }

            String name = request.getString("name");

            String token;
            String username;
            if(headers.containsKey("token")){
                token = headers.get("token").get(0);
            } else {
                token = request.getString("token");
            }

            JSONObject req = new JSONObject();
            req.put("token",token);

            JSONObject data = Requests.request(authIP + "/verifyToken",req);

            if(data.getInt("status") == 0){
                req = new JSONObject();
                req.put("token",token);

                data = Requests.request(authIP + "/tokenInfo",req);

                username = data.getString("username");
            } else {
                return getError(401,"Invalid token, " + data.getString("message"));
            }

            System.out.println(username);


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

            String token;
            String username;
            if(headers.containsKey("token")){
                token = headers.get("token").get(0);
            } else {
                token = request.getString("token");
            }

            JSONObject req = new JSONObject();
            req.put("token",token);

            JSONObject data = Requests.request(authIP + "/verifyToken",req);

            if(data.getInt("status") == 0){
                req = new JSONObject();
                req.put("token",token);

                data = Requests.request(authIP + "/tokenInfo",req);

                username = data.getString("username");
            } else {
                return getError(401,"Invalid token, " + data.getString("message"));
            }

            System.out.println(username);

            String name = request.getString("name");
            String game = request.getString("game");
            JSONObject exampleData = request.getJSONObject("data");

            Challenge challenge = challengeJpaRepository.findByNameAndGame(name,game);

            if(challenge != null){
                return ResponseEntity.ok(challenge.toJSON().toString());
            }

            challenge = new Challenge(name, request.getString("description"), game, username, exampleData);

            challengeJpaRepository.save(challenge);

            return ResponseEntity.ok(challenge.toJSON().toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/stats/challengeEntry/create")
    public ResponseEntity<String> createChallengeEntry(@RequestBody String s) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("challengeId")){
                return getError(400,"Missing challengeId");
            }

            if(!request.has("data")){
                return getError(400,"Missing data");
            }

            if(!request.has("userId")){
                return getError(400,"Missing data");
            }

            if(!request.has("score")){
                return getError(400,"Missing score");
            }

            String userId = request.getString("userId");
            JSONObject data = request.getJSONObject("data");

            Optional<Challenge> challengeOpt = challengeJpaRepository.findById(request.getString("challengeId"));

            if(!challengeOpt.isPresent()){
                return getError(400,"This challenge does not exist");
            }

            Challenge challenge = challengeOpt.get();

            JSONObject exampleData = new JSONObject(challenge.getData());

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

            ChallengeEntry entry = new ChallengeEntry(challenge.getId(), data.toString(), userId, request.getDouble("score"));

            challengeEntryJpaRepository.save(entry);

            return ResponseEntity.ok(request.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/stats/game/getAll")
    public ResponseEntity<String> getAllGames(@RequestBody String s) {
        try{
            JSONObject request = new JSONObject(s);

            JSONArray games = new JSONArray();

            logger.log(Level.INFO,"All Games");

            List<Game> gameList = gameJpaRepository.findAll();
            //gameList.sort((o1, o2) -> (int) (o1.getVersionNumber() - o2.getVersionNumber()));
            for(Game game : gameList){
                games.put(game.toJSON());
            }
            return ResponseEntity.ok(games.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/stats/challengeEntry/getAll")
    public ResponseEntity<String> getAllChallengeEntries(@RequestBody String s) {
        try{
            JSONObject request = new JSONObject(s);

            if(!request.has("pageSize")){
                return getError(400,"Missing pageSize");
            }

            if(!request.has("pageNumber")){
                return getError(400,"Missing pageNumber");
            }

            int pageSize = request.getInt("pageSize");
            int pageNumber = request.getInt("pageNumber");
            String challenge = null;
            if(request.has("challenge")){
                challenge = request.getString("challenge");
            }

            JSONArray challengeEntries = new JSONArray();

            if(challenge == null){
                List<ChallengeEntry> challengeEntryList = new ArrayList<>(challengeEntryJpaRepository.findAll());
                challengeEntryList.sort(Comparator.comparingDouble(ChallengeEntry::getScore).thenComparing(ChallengeEntry::getCreated).reversed());
                for(ChallengeEntry challengeEntry : challengeEntryList){
                    challengeEntries.put(challengeEntry.toJSON());
                }
            } else {
                List<ChallengeEntry> challengeEntryList = new ArrayList<>(getChallengeEntries(challenge,pageNumber,pageSize).toList());
                challengeEntryList.sort(Comparator.comparingDouble(ChallengeEntry::getScore).thenComparing(ChallengeEntry::getCreated).reversed());
                for(ChallengeEntry challengeEntry : challengeEntryList){
                    challengeEntries.put(challengeEntry.toJSON());
                }
            }
            return ResponseEntity.ok(challengeEntries.toString());
        }catch (Exception e){
            e.printStackTrace();
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

    public Page<ChallengeEntry> getChallengeEntries(String challengeId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return challengeEntryJpaRepository.findByChallengeId(challengeId, pageable);
    }
}
