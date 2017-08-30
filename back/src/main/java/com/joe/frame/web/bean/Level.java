package com.joe.frame.web.bean;

/**
 * 接口等级，接口等级越高优先级越高，当请求过多时优先级低的将会被最先抛弃
 * 
 * @author Administrator
 *
 */
public enum Level {
	LEVEL1(10), LEVEL2(20), LEVEL3(30), LEVEL4(40), LEVEL5(50), LEVEL6(60), LEVEL7(70), LEVEL8(80), LEVEL9(90), DEFAULT(
			50);
	private int level;

	private Level(int level) {
		this.level = level;
	}

	/**
	 * 当前等级是否大于等于目标等级
	 * 
	 * @param target
	 *            目标等级
	 * @return
	 * <li>true：当前等级大于或等于目标等级</li>
	 * <li>false：当前等级小于目标等级</li>
	 */
	public boolean higher(Level target) {
		return this.level >= target.level;
	}
	
	/**
	 * 当前等级加一（如果当前为最高等级则返回当前等级）
	 * @return
	 */
	public Level higherLevel(){
		String name = this.name();
		int nowLevel = Integer.parseInt(name.substring(name.length() - 1, name.length()));
		int higherLevel = nowLevel == 9 ? 9 : nowLevel + 1;
		name = name.substring(0 , name.length() - 1) + higherLevel;
		return Enum.valueOf(Level.class, name);
	}
	
	/**
	 * 当前等级减一（如果当前为最低等级则返回当前等级）
	 * @return
	 */
	public Level lowerLevel(){
		String name = this.name();
		int nowLevel = Integer.parseInt(name.substring(name.length() - 1, name.length()));
		int lowerLevel = nowLevel == 1 ? 1 : nowLevel - 1;
		name = name.substring(0 , name.length() - 1) + lowerLevel;
		return Enum.valueOf(Level.class, name);
	}
	
}
