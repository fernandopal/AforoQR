package es.fernandopal.aforoqr.api.email;

public class EmailTemplates {
    public static String confirmReservationEmail() {
        return """
                    <div style="border: 1px dotted #c4c4c4; background: #111111; padding: 8px;">
                        <div>
                            <p style="color: #c4c4c4">
                                Aquí tienes los detalles de la reserva que acabas de realizar:
                            </p>
                            <ul>
                                <li>
                                    Sala: {room}
                                </li>
                                <li>
                                    Fecha y hora: {time}
                                </li>
                            </ul>
                            <p>
                                <a href="{link}" style="cursor: pointer; outline: 0; color: #fff; background-color: #0d6efd; border-color: #0d6efd; display: inline-block; font-weight: 400; line-height: 1.5; text-align: center; border: 1px solid transparent; padding: 6px 12px; font-size: 16px; border-radius: .25rem;">
                                    Click para confirmar tu reserva
                                </a>
                            </p>
                        </div>
                    </div>
                """;
    }
}
