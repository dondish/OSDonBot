package DonBot.api;

import net.dv8tion.jda.core.entities.User;

public class DonCase {
    public int id;
    public int type;
    public User modid;
    public User userid;
    public String reason;

    public DonCase(int id, int type, User modid, User userid, String reason){
        this.id = id;
        this.type = type;
        this.modid = modid;
        this.userid = userid;
        this.reason = reason;
    }

}
