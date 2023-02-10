from gpiozero import LED, Button
from signal import pause

import smtplib
toAdd = 'izarktouni504@gmail.com'
smtpUser = 'iotkobbane@gmail.com'
smtpPass = 'xsnvnlmrsxgpfzix'
fromAdd = smtpUser
subject = 'Email alert'
header = 'TO:' + toAdd + '\n' + 'From:' + fromAdd + '\n' + 'Subject:' + subject
body = "I'm in a critical situation, please track my location to find me."

led = LED(25)
button = Button(2)

def button_pressed():
    led.on()
    print (header + '\n' + body)
    s = smtplib.SMTP('smtp.gmail.com',587)
    s.ehlo()
    s.starttls()
    s.ehlo()
    s.login(smtpUser,smtpPass)
    print("Login Successful")
    s.sendmail(fromAdd,toAdd,header + '\n\n' + body)
    s.sendmail(fromAdd,"chaimaaourgani84@gmail.com",header + '\n\n' + body)
    s.sendmail(fromAdd,"yousraaarab8@gmail.com",header + '\n\n' + body)
    s.sendmail(fromAdd,"elkamounihajar11@gmail.com",header + '\n\n' + body)
    print("Bouton appuyé !")
    s.quit()


def button_released():
    led.off()



button.when_pressed = button_pressed
button.when_released = button_released


pause()




