package com.jobcol.backend.NotificationService.model;

public class EmailTemplateBuilder {

    public static String buildNotificationEmail(
            String userName,
            String title,
            String message,
            String buttonText,
            String buttonLink,
            String offerTitle,
            String location
    ) {

        String html = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>JobCol</title>
        </head>

        <body style="
            margin:0;
            padding:0;
            background-color:#f3f4f6;
            font-family:Arial, Helvetica, sans-serif;
        ">

        <table width="100%" cellpadding="0" cellspacing="0" style="padding:40px 16px;">

            <tr>
                <td align="center">

                    <!-- CONTAINER -->
                    <table width="620" cellpadding="0" cellspacing="0"
                           style="
                                background:#ffffff;
                                border-radius:20px;
                                overflow:hidden;
                                box-shadow:0 10px 30px rgba(0,0,0,0.08);
                           ">

                        <!-- HEADER -->
                        <tr>
                            <td style="
                                background:linear-gradient(135deg,#4f46e5,#7c3aed);
                                padding:32px;
                            ">

                                <table width="100%">
                                    <tr>

                                        <td align="left">

                                            <table cellpadding="0" cellspacing="0">
                                                <tr>

                                                    <td style="
                                                        width:48px;
                                                        height:48px;
                                                        background:rgba(255,255,255,0.18);
                                                        border-radius:12px;
                                                        text-align:center;
                                                        vertical-align:middle;
                                                        font-size:24px;
                                                    ">
                                                        💼
                                                    </td>

                                                    <td style="padding-left:14px;">

                                                        <div style="
                                                            color:white;
                                                            font-size:30px;
                                                            font-weight:700;
                                                        ">
                                                            JobCol
                                                        </div>

                                                        <div style="
                                                            color:rgba(255,255,255,0.85);
                                                            font-size:14px;
                                                            margin-top:4px;
                                                        ">
                                                            Conecta con trabajos por horas y servicios
                                                        </div>

                                                    </td>

                                                </tr>
                                            </table>

                                        </td>

                                    </tr>
                                </table>

                            </td>
                        </tr>

                        <!-- BODY -->
                        <tr>
                            <td style="padding:48px 42px;">

                                <div style="
                                    font-size:15px;
                                    color:#6b7280;
                                    margin-bottom:18px;
                                ">
                                    Hola {{userName}} 👋
                                </div>

                                <h1 style="
                                    margin:0;
                                    font-size:32px;
                                    line-height:1.2;
                                    color:#111827;
                                    font-weight:800;
                                ">
                                    {{title}}
                                </h1>

                                <p style="
                                    margin-top:24px;
                                    color:#4b5563;
                                    font-size:17px;
                                    line-height:1.8;
                                ">
                                    {{message}}
                                </p>

                                <!-- CARD -->
                                <table width="100%" cellpadding="0" cellspacing="0"
                                       style="
                                            margin-top:28px;
                                            background:#f8fafc;
                                            border:1px solid #e5e7eb;
                                            border-radius:14px;
                                       ">

                                    <tr>
                                        <td style="padding:24px;">

                                            <div style="
                                                font-size:14px;
                                                color:#6b7280;
                                                margin-bottom:8px;
                                            ">
                                                Oferta
                                            </div>

                                            <div style="
                                                font-size:20px;
                                                font-weight:700;
                                                color:#111827;
                                            ">
                                                {{offerTitle}}
                                            </div>

                                            <div style="
                                                margin-top:12px;
                                                color:#6b7280;
                                                font-size:15px;
                                            ">
                                                📍 {{location}}
                                            </div>

                                        </td>
                                    </tr>

                                </table>

                                <!-- BUTTON -->
                                <div style="
                                    text-align:center;
                                    margin-top:40px;
                                ">

                                    <a href="{{buttonLink}}"
                                       style="
                                            display:inline-block;
                                            background:linear-gradient(135deg,#4f46e5,#7c3aed);
                                            color:white;
                                            text-decoration:none;
                                            padding:16px 34px;
                                            border-radius:14px;
                                            font-size:16px;
                                            font-weight:700;
                                            box-shadow:0 8px 20px rgba(79,70,229,0.35);
                                       ">
                                        {{buttonText}} →
                                    </a>

                                </div>

                            </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                            <td style="
                                background:#f9fafb;
                                padding:28px;
                                border-top:1px solid #e5e7eb;
                                text-align:center;
                            ">

                                <div style="
                                    color:#6b7280;
                                    font-size:14px;
                                    line-height:1.7;
                                ">
                                    Este correo fue enviado automáticamente por JobCol.
                                </div>

                                <div style="
                                    margin-top:10px;
                                    color:#9ca3af;
                                    font-size:13px;
                                ">
                                    © 2026 JobCol · Colombia
                                </div>

                            </td>
                        </tr>

                    </table>

                </td>
            </tr>

        </table>

        </body>
        </html>
        """;

        return html
                .replace("{{userName}}", userName)
                .replace("{{title}}", title)
                .replace("{{message}}", message)
                .replace("{{buttonText}}", buttonText)
                .replace("{{buttonLink}}", buttonLink)
                .replace("{{offerTitle}}", offerTitle)
                .replace("{{location}}", location);
    }

    public static String buildVerificationCodeEmail(
        String userName,
        String code
    ) {

        String html = """
        <!DOCTYPE html>
        <html lang="es">
        <head>
            <meta charset="UTF-8">
        </head>

        <body style="
            margin:0;
            padding:0;
            background-color:#f3f4f6;
            font-family:Arial, Helvetica, sans-serif;
        ">

        <table width="100%" cellpadding="0" cellspacing="0" style="padding:40px 16px;">

            <tr>
                <td align="center">

                    <table width="620" cellpadding="0" cellspacing="0"
                        style="
                                background:#ffffff;
                                border-radius:20px;
                                overflow:hidden;
                                box-shadow:0 10px 30px rgba(0,0,0,0.08);
                        ">

                        <!-- HEADER -->
                        <tr>
                            <td style="
                                background:linear-gradient(135deg,#4f46e5,#7c3aed);
                                padding:32px;
                                text-align:center;
                            ">

                                <div style="
                                    color:white;
                                    font-size:34px;
                                    font-weight:800;
                                ">
                                    JobCol
                                </div>

                                <div style="
                                    color:rgba(255,255,255,0.85);
                                    margin-top:8px;
                                    font-size:15px;
                                ">
                                    Verificación de seguridad
                                </div>

                            </td>
                        </tr>

                        <!-- BODY -->
                        <tr>
                            <td style="padding:48px 42px;">

                                <div style="
                                    font-size:16px;
                                    color:#6b7280;
                                    margin-bottom:20px;
                                ">
                                    Hola {{userName}} 👋
                                </div>

                                <h1 style="
                                    margin:0;
                                    font-size:30px;
                                    color:#111827;
                                ">
                                    Código de verificación
                                </h1>

                                <p style="
                                    margin-top:24px;
                                    font-size:17px;
                                    line-height:1.8;
                                    color:#4b5563;
                                ">
                                    Usa el siguiente código para completar tu autenticación en JobCol.
                                </p>

                                <!-- CODE BOX -->
                                <div style="
                                    margin-top:32px;
                                    background:#f5f3ff;
                                    border:2px dashed #7c3aed;
                                    border-radius:18px;
                                    padding:28px;
                                    text-align:center;
                                ">

                                    <div style="
                                        font-size:42px;
                                        letter-spacing:10px;
                                        font-weight:800;
                                        color:#4f46e5;
                                    ">
                                        {{code}}
                                    </div>

                                </div>

                                <p style="
                                    margin-top:28px;
                                    color:#6b7280;
                                    font-size:15px;
                                ">
                                    Este código expira en 5 minutos.
                                </p>

                            </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                            <td style="
                                background:#f9fafb;
                                padding:28px;
                                border-top:1px solid #e5e7eb;
                                text-align:center;
                            ">

                                <div style="
                                    color:#6b7280;
                                    font-size:14px;
                                ">
                                    Este correo fue enviado automáticamente por JobCol.
                                </div>

                            </td>
                        </tr>

                    </table>

                </td>
            </tr>

        </table>

        </body>
        </html>
        """;

        return html
                .replace("{{userName}}", userName)
                .replace("{{code}}", code);
    }
    
}