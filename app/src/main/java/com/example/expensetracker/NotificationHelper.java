package com.example.expensetracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class NotificationHelper {

    public static final String CHANNEL_ID = "expense_reminder_channel";
    public static final String CHANNEL_NAME = "Expense Reminder";
    public static final String CHANNEL_DESC = "Funny daily reminders to track expenses";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showNotification(Context context) {

        String[] titles = {
                "Expense Tracker 😎",
                "Wallet Update 💸",
                "Budget Bulletin 📰",
                "Money Patrol 🚨",
                "Savings SOS 🥲",
                "Finance Drama 🎭",
                "Pocket Alert 📢",
                "Spending Detector 👀",
                "Rupee Report ₹",
                "A message from your wallet 🥹",
                "Budget Boss Alert 📊",
                "Cash Crisis Chronicle 😵",
                "Wallet Weather Forecast ⛈️",
                "Money Mood Update 😬",
                "Expense Emergency 🚑",
                "Pocket Pressure Report 📉",
                "Daily Damage Report 💥",
                "Operation Save Money 🫡",
                "Breaking Budget News 🧾",
                "The Rupee Times ₹",
                "Wallet Health Check 🩺",
                "Spender Spotlight 🎥",
                "Expense Tracker Supreme 👑",
                "Money Control Room 🎛️",
                "Pocket Panic Mode 🚨",
                "Savings Department Notice 🏦",
                "Budget Reality Check 🪞",
                "Cashflow Confession Booth 😶",
                "Expense Investigation Unit 🔍",
                "The Wallet Whisperer 👻"
        };

        String[] openers = {
                "Bro...",
                "Listen 👀",
                "Hey spender 😏",
                "Breaking News 📰",
                "Alert 🚨",
                "Oye!",
                "Boss...",
                "Ahem 😶",
                "Excuse me, finance minister 🧾",
                "Psst 👀",
                "Captain Budget 🫡",
                "Yo money machine 💸",
                "Dear future billionaire 💼",
                "Update from your pocket 🪙",
                "Chief...",
                "My guy 😭",
                "Hello responsible citizen 🧐",
                "Dear human ATM 🏧",
                "Attention please 🎙️",
                "Knock knock 🚪",
                "Commander Cash 🪖",
                "Dear spender ji 🙏",
                "Emergency meeting! 📣",
                "Rise and track ☀️",
                "A quick update 😌",
                "Wallet calling... 📞",
                "This is awkward 😬",
                "Hello budget breaker 💥",
                "Good news... actually no 😶‍🌫️",
                "Dear tax contributor 🫠"
        };

        String[] actions = {
                "you spent again",
                "money vanished",
                "expenses rising",
                "wallet suffering",
                "budget broken",
                "another expense happened",
                "savings took damage",
                "your rupees went missing",
                "spending is getting out of hand",
                "your bank balance is nervous",
                "the wallet is under attack",
                "your money is doing parkour",
                "you are funding the economy again",
                "cash flow has become cash fly",
                "your budget is crying softly",
                "you unlocked premium spending mode",
                "money left without saying goodbye",
                "your wallet took emotional damage",
                "expenses are multiplying",
                "your savings are on vacation",
                "your balance is fighting for survival",
                "you spent like there is a cashback coming",
                "your pocket is under investigation",
                "financial discipline is missing",
                "your expenses are doing overtime",
                "money is escaping the scene",
                "you are speedrunning adulthood",
                "your budget slipped on a banana peel",
                "your wallet has trust issues now",
                "you spent first and thought later"
        };

        String[] endings = {
                "track it now 😭",
                "fix it ASAP 💀",
                "do something bro 😩",
                "before it's too late 😬",
                "your bank is scared 😨",
                "and act innocent later 😌",
                "before your wallet files a complaint 📄",
                "or tomorrow will judge you 🫠",
                "while there is still hope 🥲",
                "and maybe save yourself 😵",
                "before balance becomes history 📉",
                "or your budget will faint 😵‍💫",
                "and yes, chai also counts ☕",
                "before expenses become your personality 😭",
                "and please don’t blame inflation again 😶",
                "before your pocket starts buffering ⏳",
                "and pretend this was the plan 😌",
                "before your savings disappear in HD 📺",
                "and save your future self some pain 🫣",
                "before the month becomes a thriller 🎬",
                "and no, vibes are not a budget 😭",
                "before even your calculator gives up 🧮",
                "and maybe skip one random treat today 🍟",
                "before your wallet enters airplane mode ✈️",
                "and yes, this is officially a situation 🚨",
                "before your passbook starts roasting you 📘",
                "and don’t look away now 👀",
                "before the balance says goodbye forever 👋",
                "and maybe become financially attractive 😏",
                "before your budget turns into fiction 📖"
        };

        String[] bonusLines = {
                "That amount did not disappear magically ✨",
                "Even rich people track expenses... probably 😌",
                "One entry a day keeps confusion away 📒",
                "Your future self would really appreciate this 🙏",
                "Adulting was a scam, but tracking helps 🫥",
                "Track first, regret later 😶",
                "Your wallet deserves closure 🥹",
                "No one can save you except data 📊",
                "A budget without tracking is just a wish 🌠",
                "Financial discipline is calling... pick up 📞",
                "This is your sign to be responsible for 30 seconds ⏳",
                "If Swiggy can notify you, so can your wallet 😏",
                "Be the hero your savings deserve 🦸",
                "Today’s spending has entered the chat 💬",
                "Your bank app and this app should become friends 🤝",
                "A small entry now can prevent a big shock later 😬",
                "Tracking money is cheaper than losing it 🧠",
                "Your wallet has seen things... terrible things 😭",
                "This is not pressure, this is financial guidance 😌",
                "One update today, fewer questions tomorrow 📘",
                "You can do hard things, including expense tracking 💪",
                "Your wallet believes in you... somehow 🥲",
                "Money management is just self-care with numbers 🛁",
                "The budget did not fail, it was betrayed 😶",
                "Spending is temporary, screenshots are forever 📱",
                "No budget survives mystery spending 🕵️",
                "You’re just one entry away from feeling organized ✍️",
                "Your future version might actually thank you 🫡",
                "Keep calm and log the expense 😌",
                "This notification was sent with concern and mild panic 🚨"
        };

        String[] emojiEndings = {
                "💸",
                "📉",
                "😵",
                "🫣",
                "😭",
                "😬",
                "🧾",
                "💀",
                "🥲",
                "👀",
                "☕",
                "📊",
                "🫠",
                "🚨",
                "💰",
                "😶",
                "📘",
                "🧠",
                "😏",
                "🤡"
        };

        Random random = new Random();

        String title = titles[random.nextInt(titles.length)];

        String message = openers[random.nextInt(openers.length)] + " "
                + actions[random.nextInt(actions.length)] + ", "
                + endings[random.nextInt(endings.length)];

        String bigText = message + "\n\n"
                + bonusLines[random.nextInt(bonusLines.length)] + " "
                + emojiEndings[random.nextInt(emojiEndings.length)];

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 300, 200, 300});

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(context).notify(1001, builder.build());
        }
    }
}