<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>You're Invited to a Shared Budget!</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>
<body style="background:#e3f0ff; font-family:Segoe UI, Arial, sans-serif; margin:0; padding:0;">
    <div style="background:#fff; border-radius:18px; box-shadow:0 8px 32px rgba(25,118,210,0.13); border:2px solid #1976d2; padding:40px 28px 32px 28px; max-width:480px; margin:48px auto;">
        <div style="background:#e3f0ff; border-radius:50%; width:84px; height:84px; display:flex; align-items:center; justify-content:center; margin:0 auto 18px auto; box-shadow:0 2px 8px rgba(25,118,210,0.10); overflow:hidden;">
            <img src="https://www.shutterstock.com/image-vector/money-tree-logo-flat-color-260nw-2190185709.jpg" alt="BudgetBuddy Logo"
                 style="width:54px; height:54px; border-radius:50%; object-fit:cover; display:block; box-shadow:0 2px 8px rgba(25,118,210,0.10);" />
        </div>
        <div style="color:#1976d2; text-align:center; margin-bottom:22px; font-size:26px; font-weight:700; letter-spacing:1px;">
            You're Invited to a Shared Budget!
        </div>
        <div style="font-size:17px; margin-bottom:18px; line-height:1.7; color:#222;">
            Hello <strong style="color:#1565c0;">${invitee.username}</strong>,
        </div>
        <div style="font-size:17px; margin-bottom:18px; line-height:1.7; color:#222;">
            <strong style="color:#1565c0;">${inviter}</strong> (<a href="mailto:${inviterEmail}" style="color:#1976d2; word-break:break-all; text-decoration:underline;">${inviterEmail}</a>)
            has invited you to join the shared budget <strong style="color:#1565c0;">${budgetName}</strong>.
        </div>
        <div style="text-align:center;">
            <a href="${inviteLink}" style="background:linear-gradient(90deg,#1976d2 0%,#1565c0 100%); color:#fff !important; border-radius:8px; padding:15px 38px; font-weight:600; font-size:18px; text-decoration:none; border:none; display:inline-block; margin:32px 0 18px 0; box-shadow:0 2px 8px rgba(25,118,210,0.10);">
                Accept Invitation
            </a>
        </div>
        <div style="font-size:15px; margin-bottom:18px; color:#222;">
            Or copy and paste this link into your browser:<br />
            <a href="${inviteLink}" style="color:#1976d2; word-break:break-all; text-decoration:underline;">${inviteLink}</a>
        </div>
        <hr style="margin:36px 0 24px 0; border:none; border-top:1px solid #b3d1ff;" />
        <div style="font-size:13px; color:#1976d2; text-align:center; margin-top:10px;">
            If you did not expect this invitation, you can ignore this email.<br />
            If you have any questions, reply to this email.<br /><br />
            Best regards,<br />
            <strong style="color:#1565c0;">${inviter}</strong> from BudgetBuddy Team
        </div>
    </div>
</body>
</html>