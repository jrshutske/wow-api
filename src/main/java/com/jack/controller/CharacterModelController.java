package com.jack.controller;

import com.jack.entity.Character;
import com.jack.entity.CharacterModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import java.util.*;

/**
 * The type Character model controller.
 */
@Controller
@SpringBootApplication
public class CharacterModelController {

    /**
     * Rest rest template.
     *
     * @return the rest template
     */
    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

    /**
     * Service url string.
     *
     * @return the string
     */
    @Bean
    public String serviceURL() {
        return "https://us.battle.net/oauth/token" +
                "?client_id=f0315fe57d76491695b77140f61ffda3" +
                "&client_secret=MFVeGxsAhQyTIxWt0SJMhxaE7c87ioSv" +
                "&grant_type=client_credentials";
    }

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * Gets access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        String tokenJson = rest().getForObject(serviceURL(), String.class);
        JSONObject tokenobj = new JSONObject(tokenJson);
        String accessToken = tokenobj.getString("access_token");
        logger.info("this is the access token:" + tokenobj.getString("access_token"));
        return accessToken;
    }

    /**
     * Gets class name.
     *
     * @param id the id
     * @return the class name
     */
    public String getClassName(Integer id) {
        String classURL = "https://us.api.blizzard.com/wow/data/character/classes?locale=en_US&access_token=" + getAccessToken();
        String classJson = rest().getForObject(classURL, String.class);
        JSONObject classobj = new JSONObject(classJson);
        JSONArray jsonArray = classobj.getJSONArray("classes");
        String classname = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject classjsonobj = jsonArray.getJSONObject(i);
            if (id == classjsonobj.getInt("id")) {
                classname = classjsonobj.getString("name");
            }
        }
        logger.info("this is stims class:" + classname);
        return classname;
    }

    /**
     * Gets race faction name.
     *
     * @param id the id
     * @return the race faction name
     */
    public JSONObject getRaceFactionName(Integer id) {
        String raceURL = "https://us.api.blizzard.com/wow/data/character/races?locale=en_US&access_token=" + getAccessToken();
        String raceJson = rest().getForObject(raceURL, String.class);
        JSONObject racefactionobj = new JSONObject(raceJson);
        JSONArray jsonArray = racefactionobj.getJSONArray("races");
        JSONObject newracefactionobj = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonobj = jsonArray.getJSONObject(i);
            if (id == jsonobj.getInt("id")) {
                newracefactionobj = jsonobj;
            }
        }
        logger.info("returning this race and faction object:" + newracefactionobj);
        return newracefactionobj;
    }

    /**
     * Get character models list.
     *
     * @param characters the characters
     * @return the list
     */
    public List<CharacterModel> getCharacterModels(Set<Character> characters){
        List<CharacterModel> characterModels = new ArrayList<CharacterModel>();
        for(Character character : characters) {
            characterModels.add(getCharacterModel(character));
        }
        return characterModels;
    }

    /**
     * Get character model character model.
     *
     * @param character the character
     * @return the character model
     */
    public CharacterModel getCharacterModel(Character character){
        String characterURL = "https://us.api.blizzard.com/wow/character/" + character.getRealmname() + "/" + character.getCharactername() + "?access_token=" + getAccessToken();
        String characterJson = rest().getForObject(characterURL, String.class);
        JSONObject characterobj = new JSONObject(characterJson);
        CharacterModel characterModel = new CharacterModel();
        characterModel.setName(characterobj.getString("name"));
        characterModel.setRealm(characterobj.getString("realm"));
        characterModel.setBattlegroup(characterobj.getString("battlegroup"));
        characterModel.setClassnumber(characterobj.getInt("class"));
        characterModel.setLevel(characterobj.getInt("level"));
        characterModel.setAchievementpoints(characterobj.getInt("achievementPoints"));
        characterModel.setTotalhonorablekills(characterobj.getInt("totalHonorableKills"));
        characterModel.setId(character.getId());
        characterModel.setClassname(getClassName(characterobj.getInt("class")));
        String thumnail = characterobj.getString("thumbnail");
        String fixedthumbnail = thumnail.replaceAll("avatar","main");
        fixedthumbnail = "http://render-us.worldofwarcraft.com/character/" + fixedthumbnail;
        characterModel.setThumbnail(fixedthumbnail);
        JSONObject raceFactionObject = getRaceFactionName(characterobj.getInt("race"));
        characterModel.setRacename(raceFactionObject.getString("name"));
        String str = raceFactionObject.getString("side");
        characterModel.setFaction(str.substring(0, 1).toUpperCase() + str.substring(1));
        logger.info("this is character from the set:" + characterModel);
        return characterModel;
    }
}
