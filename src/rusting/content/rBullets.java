package rusting.rBullets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.io.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class rustingBullets implements contentList{
	public static BulletType	
	//basic bullet
	basicBulletT

	@Override
	public void load(){
	
	   basicBulletT = new BasicBulletType(1, 5, "shell"){
            width = 8f;
            height = 6f;
            
        }

	}
}
