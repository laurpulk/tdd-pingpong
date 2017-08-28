package pingis.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pingis.entities.Challenge;
import pingis.entities.CodeStatus;
import pingis.entities.TaskInstance;
import pingis.entities.User;
import pingis.services.ChallengeService;
import pingis.services.GameplayService;
import pingis.services.UserService;

@Controller
public class UserController {

  public enum LiveType {
    CONTINUE, CREATE, JOIN
  }

  @Autowired
  private UserService userService;

  @Autowired
  private ChallengeService challengeService;

  @Autowired
  private GameplayService gameplayService;

  private final Logger logger = Logger.getLogger(UserController.class);


  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Model model) {
    logger.debug("Get /login");

    return "redirect:/oauth2/authorization/code/tmc";
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public String user(Model model, Principal principal) {
    logger.debug("Get /user");

    User user = userService.handleUserAuthenticationByName(principal.getName());

    MultiValueMap<Challenge, TaskInstance> myTasksInChallenges = new LinkedMultiValueMap<>();

    user.getTaskInstances().stream()
        .filter(e -> !e.getChallenge().getIsOpen())
        .filter(e -> e.getStatus().equals(CodeStatus.DONE))
        .forEach(e -> myTasksInChallenges.add(e.getChallenge(), e));

    List<Challenge> availableChallenges = challengeService.findAll().stream()
        .filter(e -> !e.getIsOpen())
        .filter(e -> e.getLevel() <= userService.levelOfCurrentUser())
        .filter(e -> !myTasksInChallenges.containsKey(e))
        .collect(Collectors.toList());

    Challenge liveChallenge = gameplayService.getParticipatingLiveChallenge();
    Challenge randomLiveChallenge = challengeService.getRandomLiveChallenge(user);

    LiveType liveType = null;
    if (liveChallenge == null && randomLiveChallenge == null) {
      logger.debug("Live challenge type = Create");
      model.addAttribute("liveChallengeType", LiveType.CREATE);
    } else if (liveChallenge == null) {
      logger.debug("Live challenge type = Join");
      liveChallenge = randomLiveChallenge;
      model.addAttribute("liveChallengeType", LiveType.JOIN);
    } else {
      logger.debug("Live challenge type = Continue");
      model.addAttribute("liveChallengeType", LiveType.CONTINUE);
    }

    model.addAttribute("availableChallenges", availableChallenges);
    model.addAttribute("myTasksInChallenges", myTasksInChallenges);
    model.addAttribute("liveChallenge", liveChallenge);
    model.addAttribute("userLevel", userService.levelOfCurrentUser());
    model.addAttribute("user", user);

    return "user";
  }

  @RequestMapping(value = "/admin", method = RequestMethod.GET)
  public String admin(Model model) {
    logger.debug("Get /admin");

    return "admin";
  }
}
