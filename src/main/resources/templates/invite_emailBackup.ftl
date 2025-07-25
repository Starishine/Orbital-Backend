<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>You're Invited to a Shared Budget!</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <style>
        body {
            background: #f6f6f6;
            font-family: 'Segoe UI', Arial, sans-serif;
            margin: 0;
            padding: 0;
        }
        .container {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.04);
            padding: 32px 16px;
            max-width: 480px;
            margin: 40px auto;
        }
        .logo {
            display: block;
            margin: 0 auto 16px auto;
            width: 64px;
            height: 64px;
        }
        .heading {
            color: #1976d2;
            text-align: center;
            margin-bottom: 24px;
            font-size: 22px;
            font-weight: 700;
        }
        .button {
            background: #1976d2;
            color: #fff !important;
            border-radius: 4px;
            padding: 12px 24px;
            font-weight: 600;
            font-size: 16px;
            text-decoration: none;
            border: none;
            display: inline-block;
            margin: 32px 0;
            transition: background 0.2s;
        }
        .button:hover {
            background: #145ea8;
        }
        .hr {
            margin: 32px 0;
            border: none;
            border-top: 1px solid #eee;
        }
        .footer {
            font-size: 12px;
            color: #888;
            text-align: center;
        }
        .text {
            font-size: 16px;
            margin-bottom: 16px;
            line-height: 1.5;
        }
        .link {
            color: #1976d2;
            word-break: break-all;
        }
        @media only screen and (max-width: 600px) {
            .container {
                padding: 16px 4px;
                max-width: 98vw;
            }
            .heading {
                font-size: 18px;
            }
            .button {
                font-size: 15px;
                padding: 10px 16px;
            }
            .logo {
                width: 48px;
                height: 48px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <img class="logo" src="https://www.shutterstock.com/image-vector/money-tree-logo-flat-color-260nw-2190185709.jpg" alt="BudgetBuddy Logo" />
        <div class="heading">You're Invited to a Shared Budget!</div>
        <div class="text">Hi ${invitee.username},</div>
        <div class="text">
            <strong>${inviter}</strong> (<a href="mailto:${inviterEmail}" class="link">${inviterEmail}</a>) has invited you to join the shared budget <strong>${budgetName}</strong>.
        </div>
        <div style="text-align: center;">
            <a href="${inviteLink}" class="button">Accept Invitation</a>
        </div>
        <div class="text">
            Or copy and paste this link into your browser:<br />
            <a href="${inviteLink}" class="link">${inviteLink}</a>
        </div>
        <hr class="hr" />
        <div class="footer">
            If you did not expect this invitation, you can ignore this email.<br />
            If you have any questions, reply to this email.<br />
            <br />
            Best regards,<br />
            ${inviter} from BudgetBuddy Team
        </div>
    </div>
</body>
</html>