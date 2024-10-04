This is a telegram bot with an OpenAI API implementation. It uses NGROK to set up the webhook communication.

In order for the bot to work, it needs to be created an application.properties inside the following directory: src/main/java/resources/

The following properties need to be added on the file:

open.api.key="Your OpenAI api key"

open.api.url=https://api.openai.com

assistant.id="Your OpenAI Assistant ID"

thread.id="Your OpenAI Thread ID"

bot.username="Your Telegram Bot Username"

bot.token="Your Telegram Bot Token"

ngrok.domain="Your NGROK domain Ex: https://complete-car-cat.ngrok-free.app" 
ngrok.init="Your domain without https Ex: complete-car-cat.ngrok-free.app"
ngrok.port="Springboot Port"
