package com.example.mailmate.app;

import java.util.Date;
import java.util.Calendar;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Timer;

public class MainApp {

	public static void main(String[] args) {

		int year;
		int month;
		int date;
		int hour;
		int minute;
		int second;

		Timer time = new Timer();
		SimpleDateFormat std = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		Calendar calendar = new GregorianCalendar(0000,00,00,00,00,00);

		Scanner in = new Scanner(System.in);

		System.out.println("Set year: ");
		year = in.nextInt();

		System.out.println("Set month: ");
		month = in.nextInt();

		System.out.println("Set date: ");
		date = in.nextInt();

		System.out.println("Set hour: ");
		hour = in.nextInt();

		System.out.println("Set minute: ");
		minute = in.nextInt();

		System.out.println("Set second: ");
		second = in.nextInt();

		in.close();

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		System.out.println("Set date: " + std.format(calendar.getTime()));

		time.schedule(new Scheduler(), calendar.getTime(), 5);

	}
}