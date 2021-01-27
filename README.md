# Trace-GPS

Ceci est une application de suivi de position GPS.

I – Présentation des fonctionnalitées de l’application

Il est possible d’utiliser trois (3) modes :

- le mode « Suivi de traces » :  ce mode permet de faire l'enregistrement d’un parcours.
			Lorsque ce mode est sélectionné, une fenêtre de dialogue apparaîtra, seulement si un enregistrement a été mis en pause, sinon la fenêtre de dialogue n'aparaît pas. Cette fenêtre de dialogue vous laisse le choix de sois reprendre l'enregistrement ou de commencer un nouvel enregistrement. Dans ce dernier cas, l'enregistrement qui été en pause sera supprimé.
			En bas à droite de l'écran sont affichées les informations liées à la position GPS, c'est-à-dire la latitude, la longitude, la vitesse actuelle, la distance parcourus et le temps parcourus.
			La position du téléphone est enregistré de façon régulière dans un fichier.
			Il est possible de voir le contenu du fichier où l'enregistrement est effectué en appuyant sur le bouton en bas à gauche de l'écran.
			Pour arêter l'enregistrement ou le mettre en pause, il suffit de soit d'appuyer sur la flèche de retour, soit d'appuyer le bouton de retour de votre téléphone. De ce fait, une fenêtre de dialogue apparaît avec les deux options.
			Le fichier de l'enregistrement se trouve sur le téléphone à cet emplacement (par défaut) : Android/data/com.example.tracesgps/files/Tracks.

- le mode « Parcours Autonome » : ce mode permet de suivre un parcours.
			Lorsque ce mode est sélectionné, une fenêtre apparaîtra pour la sélection d'un fichier contenant le parcours.
			En bas à gauche de l'écran sont affichées les informations liées à la position GPS, c'est-à-dire la latitude, la longitude, la vitesse actuelle, la distance parcourus et le temps parcourus.
			En bas à droite de l'écran sont affichées les informations liées à la prochaine balise dans le parcours, plus précisément la direction, la distance et le délai.
			Vous pouvez à tout moment quitter le mode en appuyant sur la flèche retour dans la barre outils ou sur la touche retour de votre téléphone.
			Quand vou avez fini de suivre le parcours, une fenêtre de dialogue apparaîtra et vous pourrez visualiser vos statistiques. Vous aurez la possibiliter de valider le parcours que vous venez de faire, et dans ce cas le fichier du suivi de traces sera envoyé au serveur.
			Vous pouvez voir les fichiers qui sont sur le serveur au lien suivant : https://tracegps-2020.herokuapp.com/getsuivi?id=* (*numéro).
			
- le mode « Parcours Connecté » : ce mode permet de suivre un parcours avec un envoi de la position vers un serveur.
			Lorsque ce mode est sélectionné, une fenêtre de dialogue apparaîtra pour vous demander votre numéro de concurrent.
			Ensuite, comme pour le mode « Parcours Autonome », une fenêtre apparaîtra pour la sélection d'un fichier contenant le parcours.
			Les mêmes informations sur la position GPS et la prochaine balise du parcours sont affichées.
			Dès que la position GPS du téléphone est enregistré dans le fichier, elle est aussi envoyé au serveur.
			Vous pouvez suivre la position d'un concurrent au lien suivant : http://tracegps-2020.herokuapp.com/suivigps?id=* (* numéro du concurrent).
			Lorsque le suivi du parcours est fini, les statistiques sont affichées.

L’application propose également d'autres fonctionnalités :

- Affichage d'une carte du monde.
			La carte est centrée sur la position actuelle de l'apareil par défaut.
			Il est possible de :
				- recentrer la carte sur la position actuelle de l'apareil en appuyant sur le bouton en haut à droite de l'écran.
				- déplacer la carte par glissement avec un doigt sur l’écran.
				- zoomer et de dézoomer en écartant ou en rapprochant deux (2) doigts sur l’écrans ou encore en utilisant les boutons + et – sur l’écran.

- Visualisation de statistiques sur les parcours enregistré avec le mode « Suivi de traces ».

- Si vous êtes perdu, vous pouvez envoyer un SMS, composé de votre position actuelle, depuis l'application.

- L'accès aux paramètres liés à l'enregistrement d'un parcours.

- L'aide (ce fichier).
				
