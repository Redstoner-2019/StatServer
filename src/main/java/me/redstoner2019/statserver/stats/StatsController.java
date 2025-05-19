package me.redstoner2019.statserver.stats;

import me.redstoner2019.statserver.stats.challenge.Challenge;
import me.redstoner2019.statserver.stats.challenge.entry.ChallengeEntry;
import me.redstoner2019.statserver.stats.challenge.ChallengeJpaRepository;
import me.redstoner2019.statserver.stats.challenge.entry.ChallengeEntryJpaRepository;
import me.redstoner2019.statserver.stats.challenge.entry.JsonValueType;
import me.redstoner2019.statserver.stats.challenge.version.ChallengeVersion;
import me.redstoner2019.statserver.stats.challenge.version.ChallengeVersionJpaRepository;
import me.redstoner2019.statserver.stats.game.Game;
import me.redstoner2019.statserver.stats.game.GameJpaRepository;
import me.redstoner2019.statserver.stats.util.AuthenticationHelper;
import me.redstoner2019.statserver.stats.util.AuthenticationResult;
import me.redstoner2019.statserver.stats.util.HeadersToJson;
import me.redstoner2019.statserver.stats.version.Version;
import me.redstoner2019.statserver.stats.version.VersionJpaRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
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
                //init();
                logger.log(Level.INFO,"Init completed.");
            }
        }).start();
    }

    int status = 0;

    public void logStatus(int max){
        logger.log(Level.INFO,"Completed " + String.format("%d / %d (%.2f %%)",status,max, ((double) status / max) * 100.0));
    }

    public void init(){
        logger.log(Level.INFO,"Init completed.");
        status=0;
        //List<String> users = List.of("Alessia","Akira","Redstoner_2019","aru","lachso","theytrin","nicklas","alex","gansi","lami","flao");
        List<String> users = new ArrayList<>(List.of("Luki", "Krankrichter", "Reicher Rentner", "Nico Kacki", "Dinis Köter", "Phillip Vincent Giovanni Elberfeld", "Jade", "Mischa", "marsel", "Mirco", "Amir", "Sergej", "Maggsimus Primus"));
        users.clear();
        for (int i = 0; i < 35; i++) {
            users.add("User " + (i + 1));
        }

        users.clear();

        logStatus(2 + users.size());

        gameJpaRepository.deleteAll();
        challengeJpaRepository.deleteAll();
        challengeEntryJpaRepository.deleteAll();
        versionJpaRepository.deleteAll();
        challengeVersionJpaRepository.deleteAll();

        status++;
        logStatus(2 + users.size());

        String username = "Redstoner_2019";

        Game game = new Game("FNaF", username,"/minecraft.png");

        gameJpaRepository.save(game);

        JSONObject data = new JSONObject();

        data.put("powerLeft",0.1);
        data.put("death","foxy");
        data.put("place",1);

        Challenge ventablack = new Challenge("Ventablack","Ventablack test",game.getId(),username,data);
        Challenge night6 = new Challenge("Night 6","Night 6 test",game.getId(),username,data);

        challengeJpaRepository.save(ventablack);
        challengeJpaRepository.save(night6);

        versionJpaRepository.save(new Version(game.getId(),"1.0.0",0));
        versionJpaRepository.save(new Version(game.getId(),"1.1.0",1));
        versionJpaRepository.save(new Version(game.getId(),"1.2.0",2));
        versionJpaRepository.save(new Version(game.getId(),"1.3.0",3));
        versionJpaRepository.save(new Version(game.getId(),"1.4.0",4));
        versionJpaRepository.save(new Version(game.getId(),"1.5.0",5));

        //challengeVersionJpaRepository.save(new ChallengeVersion("1.0.0",ventablack.getId()));


        //Version v130 = new Version(game.getId(),"1.3.0",3);

        //versionJpaRepository.save(new Version(game.getId(),"1.0.0",0));
        //versionJpaRepository.save(new Version(game.getId(),"1.1.0",1));
        //versionJpaRepository.save(new Version(game.getId(),"1.2.0",2));
        //versionJpaRepository.save(v130);

        challengeVersionJpaRepository.save(new ChallengeVersion("1.0.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.1.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.2.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.3.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.4.0",ventablack.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.5.0",ventablack.getId()));

        challengeVersionJpaRepository.save(new ChallengeVersion("1.3.0",night6.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.4.0",night6.getId()));
        challengeVersionJpaRepository.save(new ChallengeVersion("1.5.0",night6.getId()));

        status++;
        logStatus(2 + users.size());

        for(String user : users){
            for (int i = 0; i < 2; i++) {
                data.put("powerLeft",Math.floor(Math.random() * 1000) / 1000);
                data.put("place",i);

                ChallengeEntry entry = new ChallengeEntry();
                entry.setChallengeId(ventablack.getId());
                entry.setGameId(game.getId());
                entry.setUsername(user);
                entry.setVersion("1.3.0");
                entry.setData(data.toString());
                challengeEntryJpaRepository.save(entry);
            }
            status++;
            logStatus(2 + users.size());
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

            Optional<Game> gameOpt = gameJpaRepository.findByName(name);

            if(gameOpt.isPresent()){
                return getError(400,"This game already exists");
            }


            String icon = request.optString("icon","/default.png");

            Game game = new Game(name, username, icon);
            gameJpaRepository.save(game);

            JSONObject response = new JSONObject();
            response.put("result", "created");
            response.put("game", game.toJSON());

            return ResponseEntity.ok(response.toString());
        }catch (Exception e){
            e.printStackTrace();
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

            ChallengeEntry entry = new ChallengeEntry(UUID.randomUUID().toString(),request.getString("game"),challenge.getId(),username,request.getString("version"), data.toString(), LocalDateTime.now());

            challengeEntryJpaRepository.save(entry);

            return ResponseEntity.ok(request.toString());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Getting
     */

    @RequestMapping(value="/stats/game/getAll", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getAllGames(@RequestHeader HttpHeaders headers) {
        try{
            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(new JSONObject(),headers);

            System.out.println(authResult.getMessage());

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            JSONArray games = new JSONArray();

            //logger.log(Level.INFO,"All Games");

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

    @RequestMapping(value="/stats/versions/getAll", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getAllVersions(@RequestBody(required = false) String s, @RequestHeader HttpHeaders headers) {
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
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

    @RequestMapping(value="/stats/challenges/getAll", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getAllChallenges(@RequestBody(required = false) String s, @RequestHeader HttpHeaders headers) {
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
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

    @RequestMapping(value="/stats/challenges/get", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getChallenge(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
            JSONObject request = new JSONObject(s);

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            if (request.has("uuid")){
                Optional<Challenge> gameOpt = challengeJpaRepository.findById(request.getString("uuid"));
                return gameOpt.map(game -> ResponseEntity.ok(game.toJSON().toString())).orElseGet(() -> getError(404, "uuid not found"));
            } else {
                return getError(400,"Missing UUID, or invalid game description");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    /*@RequestMapping(value="/stats/challengeEntry/getAll", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getAllChallengeEntries(@RequestBody(required = false) String s, @RequestHeader HttpHeaders headers) {
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
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
    }*/

    @RequestMapping(value="/stats/recentRuns/getAll", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getAllRecentRuns(@RequestBody(required = false) String s, @RequestHeader HttpHeaders headers) {
        System.out.println("Test");
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
            JSONObject request = new JSONObject(s);

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            JSONArray runs = new JSONArray();

            for (int i = 0; i < 30; i++) {
                JSONObject run = new JSONObject();
                run.put("id", i + "-A");
                run.put("name", "Test Run " + i);
                run.put("scoreText","15:23");
                run.put("imageUrl","https://preview.redd.it/sut6soi9gcu51.jpg?width=1080&crop=smart&auto=webp&s=c537958466522a0c7a477658eb85a1f9da0eb68b");
                runs.put(run);
            }
            return ResponseEntity.ok(runs.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }


    @RequestMapping(value="/stats/challengeEntry/getAll/pages", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getAllChallengeEntriesPages(@RequestBody(required = false) String s, @RequestHeader HttpHeaders headers) {
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
            JSONObject request = new JSONObject(s);

            if(!request.has("pageSize")){
                return getError(400,"Missing pageSize");
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
            String challenge = request.getString("challenge");
            String game = request.getString("game");
            String version = request.getString("version");

            JSONObject result = new JSONObject();

            //result.put("pages",getChallengeEntriesPages(challenge,game, version, pageSize));

            return ResponseEntity.ok(result.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @RequestMapping(value="/stats/challengeEntry/getAllSorted", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getAllSorted(@RequestBody(required = false) String s, @RequestHeader HttpHeaders headers){
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
            JSONObject jsonObject = new JSONObject(s);

            int pageNumber = jsonObject.getInt("pageNumber");
            int pageSize = jsonObject.getInt("pageSize");

            String challenge = jsonObject.getString("challenge");
            String game = jsonObject.getString("game");
            String version = jsonObject.getString("version");

            boolean ascending = !jsonObject.has("ascending") || jsonObject.getBoolean("ascending");
            String sortBy = jsonObject.has("sortBy") ? jsonObject.getString("sortBy") : "score";

            Page<ChallengeEntry> p = challengeEntryJpaRepository.findAll(PageRequest.of(0, 1));
            ChallengeEntry ce = p.iterator().next();

            JsonValueType type;

            switch (new JSONObject(ce.getData()).get(sortBy).getClass().getSimpleName()) {
                case "Double", "BigDecimal" -> type = JsonValueType.DOUBLE;
                case "Integer" -> type = JsonValueType.INTEGER;
                case "Long" -> type = JsonValueType.INTEGER;
                case "String" -> type = JsonValueType.STRING;
                default -> type = JsonValueType.DOUBLE;
            }

            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<ChallengeEntry> page = challengeEntryJpaRepository.findTopEntriesPerUser(game, challenge, version, sortBy, type, !ascending, pageable);

            JSONArray result = new JSONArray();

            Iterator<ChallengeEntry> iterator = page.iterator();
            while (iterator.hasNext()) {
                ChallengeEntry challengeEntry = iterator.next();
                result.put(challengeEntry.toJSON());
            }

            JSONObject resultObject = new JSONObject();
            resultObject.put("result",result);
            resultObject.put("pages",challengeEntryJpaRepository.countPagesForTopEntries(game,challenge,version,pageSize));

            return ResponseEntity.ok(resultObject.toString());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @RequestMapping(value="/test", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> test(@RequestBody(required = false) String s, @RequestHeader HttpHeaders headers){
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
            JSONObject request = new JSONObject(s);

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            String username = authResult.getUsername();

            return ResponseEntity.ok(request.toString());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }




    @RequestMapping(value="/stats/game/get", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getGame(@RequestBody String s, @RequestHeader HttpHeaders headers) {
        try{
            if(s == null) s = HeadersToJson.headersToJson(headers).toString();
            JSONObject request = new JSONObject(s);

            AuthenticationResult authResult = AuthenticationHelper.verifyAuth(request,headers);

            if(!authResult.isSuccess()){
                return getError(authResult.getStatus(),authResult.getMessage());
            }

            if (request.has("uuid")){
                Optional<Game> gameOpt = gameJpaRepository.findById(request.getString("uuid"));
                return gameOpt.map(game -> ResponseEntity.ok(game.toJSON().toString())).orElseGet(() -> getError(404, "uuid not found"));
            } else if (request.has("name")){
                Optional<Game> gameOpt = gameJpaRepository.findByName(request.getString("name"));
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

    /*public Page<ChallengeEntry> getChallengeEntries(String challengeId,String game, String version, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return challengeEntryJpaRepository.findByChallengeIdAndGameAndVersion(challengeId,game,version, pageable);
    }

    public int getChallengeEntriesPages(String challengeId,String game, String version, int size) {
        PageRequest pageable = PageRequest.of(0, size);
        return challengeEntryJpaRepository.findByChallengeIdAndGameAndVersion(challengeId,game,version, pageable).getTotalPages();
    }*/
}
