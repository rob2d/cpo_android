package com.rob2d.android.textui;

public interface Prompter
{
	public void showMsg(String promptMsg, String titleMsg);
	public String showInputPrompt(String promptMsg, String titleMsg);
	public boolean showDualOption(String promptMsg, String titleMsg, String button1, String button2);
	public int showTripleOption(String promptMsg, String titleMsg, String button1, String button2, String button3);
}
