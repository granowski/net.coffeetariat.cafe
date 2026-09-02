#!/bin/zsh
# Cafe Service Demo Script
# Automates Auth Token retrieval and Storage operations

AUTH_BASE="http://localhost:8080"
CAFE_BASE="http://localhost:8666"
CLIENT_ID="demo-client-$(date +%s)"
KEY_FILE="client_private.pem"

echo "=== Cafe API Automation Demo ==="
echo "Client ID: $CLIENT_ID"

# Check for dependencies
for cmd in jq openssl curl; do
  if ! command -v $cmd >/dev/null; then
    echo "Error: $cmd is not installed."
    exit 1
  fi
done

# Embedded Q&A Map
typeset -A JOKES
echo "[1/6] Loading jokes..."
JOKES=(
    "Why did the scarecrow win an award?" "Because he was outstanding in his field!"
    "Why don’t skeletons fight each other?" "They don’t have the guts."
    "What do you call fake spaghetti?" "An impasta!"
    "Why did the math book look so sad?" "Because it had too many problems."
    "Why can’t you give Elsa a balloon?" "Because she’ll let it go."
    "What do you call cheese that isn’t yours?" "Nacho cheese!"
    "Why couldn’t the bicycle stand up by itself?" "It was two-tired."
    "What do you call a fish wearing a bowtie?" "Sofishticated."
    "Why did the golfer bring two pairs of pants?" "In case he got a hole in one."
    "Why did the computer go to the doctor?" "It had a virus!"
    "How do you organize a space party?" "You planet."
    "Why did the cookie go to the hospital?" "Because it felt crumby."
    "What do you call a bear with no teeth?" "A gummy bear."
    "Why did the tomato turn red?" "Because it saw the salad dressing!"
    "What do you call a can opener that doesn’t work?" "A can’t opener."
    "Why did the picture go to jail?" "Because it was framed."
    "Why are elevator jokes so classic and good?" "They work on many levels."
    "Why don’t eggs tell jokes?" "They’d crack each other up."
    "What do you call an alligator in a vest?" "An investigator."
    "How does the ocean say hello?" "It waves."
    "Why don’t scientists trust atoms?" "Because they make up everything."
    "What do you call a boomerang that won’t come back?" "A stick."
    "Why did the gym close down?" "It just didn’t work out."
    "Why did the orange stop rolling down the hill?" "It ran out of juice."
    "What do you call a sleeping bull?" "A bulldozer."
    "Why did the kid bring a ladder to school?" "Because they wanted to go to high school."
    "What do you call a snowman with a six-pack?" "An abdominal snowman."
    "Why did the man run around his bed?" "Because he was trying to catch up on his sleep."
    "Why can’t your nose be 12 inches long?" "Because then it would be a foot."
    "What do you call a belt made of watches?" "A waist of time."
    "Why did the scarecrow keep getting promoted?" "Because he was outstanding in his field."
    "What do you call a factory that makes okay products?" "A satisfactory."
    "What did the zero say to the eight?" "Nice belt!"
    "How do you make a tissue dance?" "Put a little boogie in it."
    "Why did the math teacher plant a tree?" "She wanted to see natural logs."
    "What’s orange and sounds like a parrot?" "A carrot."
    "Why was the broom late?" "It overswept."
    "What do you call a line of men waiting to get haircuts?" "A barberqueue."
    "Why did the stadium get hot after the game?" "All the fans left."
    "Why was the calendar nervous?" "Its days were numbered."
    "What kind of music do mummies listen to?" "Wrap music."
    "Why did the chicken join a band?" "Because it had the drumsticks."
    "Why did the man put his money in the freezer?" "He wanted cold hard cash."
    "What do you call a very small Valentine?" "A valen-tiny."
    "Why did the barber win the race?" "He knew a shortcut."
    "What did the grape do when it got stepped on?" "Nothing, it just let out a little wine."
    "What do you call a fish with no eyes?" "Fsh."
    "Why did the coffee cup file a police report?" "It got mugged."
    "What do you call a pile of cats?" "A meowtain."
    "Why did the music teacher need a ladder?" "To reach the high notes."
    "Why was the computer cold?" "It left its Windows open."
    "Why don’t oysters donate to charity?" "Because they’re shellfish."
    "What do you call a dinosaur with an extensive vocabulary?" "A thesaurus."
    "Why did the banana go to the doctor?" "Because it wasn’t peeling well."
    "Why was the math lecture so long?" "The professor kept going off on a tangent."
    "What do you call a lazy kangaroo?" "A pouch potato."
    "What do you call two birds in love?" "Tweethearts."
    "Why did the music teacher go to jail?" "Because she got caught with too many sharp objects."
    "Why did the cookie go to the doctor?" "It was feeling crumby."
    "What do you call a nervous javelin thrower?" "Shakespeare."
    "Why did the belt get arrested?" "It held up a pair of pants."
    "How do you catch a whole school of fish?" "With bookworms."
    "Why did the banker switch careers?" "She lost interest."
    "What do you call a dog magician?" "A labracadabrador."
    "Why couldn’t the leopard play hide and seek?" "Because he was always spotted."
    "What time did the man go to the dentist?" "Tooth-hurty."
    "Why are ghosts bad liars?" "Because they are too transparent."
    "What do you call a cow with no legs?" "Ground beef."
    "What do you call a cow with two legs?" "Lean beef."
    "What do you call a cow that just gave birth?" "De-calf-inated."
    "Why don’t crabs give to charity?" "Because they’re shellfish."
    "What do you call a sheep covered in chocolate?" "A candy baa."
    "What do you call a fish that practices medicine?" "A sturgeon."
    "Why don’t calendars ever panic?" "They’ve got their days numbered."
    "What did the ocean say to the beach?" "Nothing, it just waved."
    "Why did the smartphone need glasses?" "It lost all its contacts."
    "Why was the equal sign so humble?" "Because it knew it wasn’t less than or greater than anyone else."
    "Why did the candle quit?" "It was burned out."
    "Why was the belt so stressed?" "It was always under a lot of pressure."
    "What’s a computer’s favorite snack?" "Computer chips."
    "How do trees access the internet?" "They log in."
    "Why did the book join the police?" "It wanted to go undercover."
    "What’s a skeleton’s least favorite room?" "The living room."
    "Why are piggy banks so wise?" "They’re full of common cents."
    "What’s a potato’s favorite form of transportation?" "The gravy train."
    "What do you get from a pampered cow?" "Spoiled milk."
    "Why did the frog take the bus to work?" "His car was toad."
    "What do you call a group of disorganized cats?" "A cat-astrophe."
    "What did one wall say to the other wall?" "I’ll meet you at the corner."
    "Why did the bee get married?" "Because he found his honey."
    "Why couldn’t the pirate play cards?" "Because he was sitting on the deck."
    "What do you call a deer with no eyes?" "No eye-deer."
    "How do you make a lemon drop?" "Just let it fall."
    "Why did the man put his car in the oven?" "He wanted a hot rod."
    "What do you call a singing laptop?" "A Dell."
    "Why did the musician bring a ladder on stage?" "To reach the high notes."
    "Why did the bicycle fall over?" "Because it was two-tired."
    "What do you call an elephant that doesn’t matter?" "An irrelephant."
    "Why did the golfer bring an extra shirt?" "In case he got a hole in one."
    "Why did the computer sit on the floor?" "It wanted to crash."
    "Why did the scarecrow become a successful neurosurgeon?" "Outstanding in his field and great with brains."
    "Why did the clock get in trouble in class?" "It tocked too much."
    "What do you call a knight who is afraid to fight?" "Sir Render."
    "What do you call a pencil with two erasers?" "Pointless."
    "Why did the stadium light blush?" "It saw the other team’s fans."
    "What did the big flower say to the little flower?" "Hi, bud!"
    "Why did the mushroom get invited to the party?" "Because he’s a fungi."
    "Why can’t a leopard hide?" "He’s always spotted."
    "What do you call an apology written in dots and dashes?" "Re-morse code."
    "Why did the tomato turn red at the party?" "Because it saw the salad dressing."
    "What do you call a sleeping T-Rex?" "A dino-snore."
    "Why did the music note get good grades?" "Because it was sharp."
    "What do you call an astronaut’s favorite part of a computer?" "The space bar."
    "Why did the baker go to therapy?" "He kneaded it."
    "What kind of key opens a banana?" "A monkey."
    "Why was the broom promoted?" "It swept the competition."
    "What do you call a boisterous bouquet?" "Flower power."
    "Why did the music student bring string to class?" "To tie the notes together."
    "What do you call a cold dog?" "A chili dog."
    "Why did the smartphone go to school?" "To improve its cell-f."
    "Why was the computer’s voice so calm?" "It had good control of its CAPS."
    "Why do bees have sticky hair?" "Because they use honeycombs."
    "What do you call a cat on the beach?" "Sandy claws."
    "What’s a frog’s favorite candy?" "Lollihops."
    "Why did the belt refuse dessert?" "It was already on the last notch."
    "Why did the flashlight get good grades?" "It was really bright."
    "Why did the egg hide?" "It was a little chicken."
    "What do you call a pig that practices karate?" "A pork chop."
    "What do you call a sleeping pizza?" "A nap-olitano."
    "Why did the banana go to school?" "To become a smart peel."
    "Why did the picture take a vacation?" "It needed to get out of the frame."
    "Why did the tree worry about its grades?" "Too many trunks and not enough branches."
    "What do you call a bee that can’t make up its mind?" "A may-bee."
    "Why did the strawberry cry?" "It was in a jam."
    "What do you call a sleeping potato?" "A yawn-tato."
    "Why did the music note go to art class?" "It wanted to draw a treble."
    "What do you call a rabbit with fleas?" "Bugs Bunny."
    "Why don’t seagulls fly over the bay?" "Because then they’d be bagels."
    "Why did the keyboard break up with the computer?" "It wasn’t their type."
    "Why did the candle study for the test?" "It wanted to be a bright student."
    "What did one plate say to the other plate?" "Dinner’s on me."
    "Why did the sheep cross the road?" "To get to the baaa-ber shop."
    "What do you call a bike that falls over a lot?" "A tumble-cycle."
    "Why did the tomato sit down?" "It needed to ketchup on rest."
    "What do you call a horse that lives next door?" "A neigh-bor."
    "Why did the music teacher sit on a ladder?" "She wanted a new perspective on the scales."
    "Why did the cloud stay home?" "It felt under the weather."
    "What’s a vampire’s favorite fruit?" "A blood orange."
    "Why did the pencil cross the road?" "To draw attention."
    "What do you call a bear in the rain?" "A drizzly bear."
    "Why did the melon have a big wedding?" "Because it cantaloupe."
    "What do you call a dog that can tell time?" "A watch-dog."
    "Why did the grape stop in the road?" "It ran out of juice."
    "What’s a pirate’s favorite letter?" "You’d think it’s R, but his first love be the C."
    "Why did the computer go to art class?" "It had too many windows to draw."
    "What did the left eye say to the right eye?" "Between us, something smells."
    "Why did the bicycle bring a notebook?" "To take notes on its cycles."
    "Why did the snowman look through a bag of carrots?" "He was picking his nose."
    "What do you call a unicorn with a cold?" "Achoo-nicorn."
    "Why did the banana wear sunscreen?" "It didn’t want to peel."
    "Why did the music book go to therapy?" "Too many unresolved issues."
    "What do you call a parade of rabbits hopping backwards?" "A receding hare-line."
    "Why was the belt always calm?" "It had great buckle control."
    "What do you call an owl that does magic?" "Hoodini."
    "Why did the watch apply for a job?" "It had time on its hands."
    "What do you call an overcaffeinated cow?" "Deja-moo."
    "Why did the music teacher get locked out?" "She lost the key."
    "Why did the astronaut break up?" "They needed space."
    "What do you call an ant who fights crime?" "A vigil-ant."
    "Why did the broom get a medal?" "For sweeping achievements."
    "Why was the tomato embarrassed?" "It saw the salad dressing."
    "Why did the chicken go to the séance?" "To talk to the other side."
    "Why did the bicycle go to therapy?" "It had too many cycles."
    "What do you call a frog with a broken leg?" "Unhoppy."
    "Why did the lemon fail the test?" "It was too sour about the questions."
    "What do you call a sleeping dragon?" "A snore-cerer."
    "Why did the kiwi go to the doctor?" "It was feeling a little fuzzy."
    "What do you call a dog who builds houses?" "A bark-itect."
    "Why did the librarian get kicked off the plane?" "It was overbooked."
    "What do you call a helpful duck?" "A quack of all trades."
    "Why did the cookie sit by the computer?" "To delete crumbs."
    "What do you call a star that likes to read?" "A book-light."
    "Why did the smartphone sit in the sun?" "To get more bars."
    "Why did the plant go to school?" "It wanted to improve its STEM."
    "What do you call a very fast salad?" "A rocket."
    "Why did the lettuce break up with the tomato?" "It couldn’t ketchup."
    "Why was the keyboard a great friend?" "It was always there for support."
    "What do you call a musical insect?" "A hum-bug."
    "Why did the cup say sorry?" "It mugged someone."
    "Why did the cow become an astronaut?" "To see the moooon."
    "What do you call a polite onion?" "A civil onion."
    "Why did the tomato avoid the fridge?" "It couldn’t handle the chill."
    "What do you call a deer with good manners?" "Proper doe-corum."
    "Why did the notebook enroll in art class?" "It had a lot to draw upon."
    "What do you call a crab that plays baseball?" "A pinch hitter."
    "Why did the light bulb get good grades?" "It was brilliant."
    "Why did the tennis ball stop?" "It was out of bounce."
    "What do you call a careful wolf?" "A ware-wolf."
    "Why did the orange apply for a job?" "It wanted to concentrate."
    "Why did the mirror go to school?" "To reflect on itself."
    "Why did the snowboarder bring a broom?" "To sweep the slopes."
    "Why did the hairbrush get promoted?" "It handled tangles well."
    "What do you call a sunburned librarian?" "Well red."
    "What do you call a boomerang that’s lost?" "A stick."
    "Why did the ghost go to school?" "To improve his boo-k smarts."
    "What do you call a fashionable snake?" "Hiss-ter chic."
    "Why did the peanut call the police?" "It was a-salted."
    "Why did the gardener bring a light to the garden?" "To grow light bulbs."
    "Why did the fisherman bring a computer?" "To improve his net work."
    "What do you call a musical whale?" "An orca-stra."
    "Why did the grape get promoted?" "It was raisin the bar."
    "Why did the robot go on a diet?" "It had too many bytes."
    "Why did the drum take a nap?" "It was beat."
    "Why did the book ask for help?" "It had too many issues."
    "Why did the calendar get a job?" "It had a lot of dates."
    "What do you call a baker who sings?" "A croissant-o."
    "Why did the web developer go broke?" "Because he used up all his cache."
    "Why did the cloud get detention?" "It was too thunder-ous."
    "Why did the carrot win the race?" "It had a-peeling speed."
    "Why did the zipper start a podcast?" "It wanted to open up."
    "Why did the sandwich go to the gym?" "To get breader."
    "Why did the soda can go to school?" "To get a little can-dor."
    "Why did the battery go to therapy?" "It needed a recharge in life."
    "Why did the librarian bring a ladder?" "To reach the higher shelves of knowledge."
    "Why did the note fail music class?" "It couldn’t stay on key."
    "Why did the salad apply for a job?" "It wanted to turn over a new leaf."
    "Why did the blanket get promoted?" "It covered for everyone."
    "Why did the stapler make a great leader?" "It kept everything together."
    "Why did the painter always win arguments?" "He had the final brushstroke."
    "Why did the clock apply for a loan?" "It needed more time."
    "Why did the keyboard sleep well?" "It was comfortable with its type."
    "Why did the microphone feel important?" "It was always heard."
    "Why did the backpack get an award?" "It carried the team."
    "Why did the notebook blush?" "It was full of sensitive information."
    "Why did the ruler get invited to every party?" "It was the measure of fun."
    "Why did the calculator cross the road?" "To sum up the situation."
    "Why did the puzzle piece look happy?" "It finally fit in."
)
echo "  Jokes loaded: ${#JOKES}"

# 1. Register Client & Get Private Key
echo "[2/6] Registering client and retrieving private key..."
curl -s "$AUTH_BASE/api/clients/$CLIENT_ID/new-private-key" > "$KEY_FILE"
if [ ! -s "$KEY_FILE" ]; then
  echo "Failed to get private key. Is Gryptography service running on $AUTH_BASE?"
  rm -f "$KEY_FILE"
  exit 1
fi

# 2. Get Challenge
echo "[3/6] Requesting authentication challenge..."
CHALLENGE_JSON=$(curl -s "$AUTH_BASE/api/challenge?clientId=$CLIENT_ID")
SESSION_ID=$(echo "$CHALLENGE_JSON" | jq -r '.sessionId' 2>/dev/null)
INQUIRY=$(echo "$CHALLENGE_JSON" | jq -r '.inquiry' 2>/dev/null)

if [[ -z "$SESSION_ID" || "$SESSION_ID" == "null" || -z "$INQUIRY" || "$INQUIRY" == "null" ]]; then
  echo "Failed to get challenge. Response: $CHALLENGE_JSON"
  rm -f "$KEY_FILE"
  exit 1
fi

# 3. Decrypt Challenge & Match Answer
echo "[4/6] Decrypting challenge and finding answer..."
# Try OAEP SHA256/SHA1 (Default for Java OAEPWithSHA-256AndMGF1Padding)
QUESTION=$(echo "$INQUIRY" | base64 -d | openssl pkeyutl -decrypt -inkey "$KEY_FILE" -pkeyopt rsa_padding_mode:oaep -pkeyopt rsa_oaep_md:sha256 -pkeyopt rsa_mgf1_md:sha1 2>/dev/null)

if [[ -z "$QUESTION" ]]; then
  # Try OAEP SHA256/SHA256
  QUESTION=$(echo "$INQUIRY" | base64 -d | openssl pkeyutl -decrypt -inkey "$KEY_FILE" -pkeyopt rsa_padding_mode:oaep -pkeyopt rsa_oaep_md:sha256 -pkeyopt rsa_mgf1_md:sha256 2>/dev/null)
fi

if [[ -z "$QUESTION" ]]; then
  # Fallback to PKCS1
  QUESTION=$(echo "$INQUIRY" | base64 -d | openssl pkeyutl -decrypt -inkey "$KEY_FILE" 2>/dev/null)
fi

if [[ -z "$QUESTION" ]]; then
  echo "Failed to decrypt challenge inquiry."
  rm -f "$KEY_FILE"
  exit 1
fi

# Robust trimming
QUESTION=$(echo -n "$QUESTION" | tr -d '\r' | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')

echo "  Challenge Question: $QUESTION"
ANSWER="${JOKES[$QUESTION]}"

if [[ -z "$ANSWER" ]]; then
  echo "Answer not found in embedded jokes for: '$QUESTION'"
  echo "Hex dump of question:"
  echo -n "$QUESTION" | hexdump -C
  rm -f "$KEY_FILE"
  exit 1
fi
echo "  Matched Answer: $ANSWER"

# 4. Sign Answer & Exchange for Token
echo "[5/6] Signing answer and exchanging for JWT token..."
SIGNATURE=$(printf "%s" "$ANSWER" | openssl dgst -sha256 -sign "$KEY_FILE" | base64 | tr -d '\n')
TOKEN_JSON=$(curl -s -X POST "$AUTH_BASE/api/answer?clientId=$CLIENT_ID" \
  --data-urlencode "sessionId=$SESSION_ID" \
  --data-urlencode "answer=$SIGNATURE")

TOKEN=$(echo "$TOKEN_JSON" | jq -r '.token' 2>/dev/null)

if [[ -z "$TOKEN" || "$TOKEN" == "null" ]]; then
  echo "Failed to get token. Response: $TOKEN_JSON"
  rm -f "$KEY_FILE"
  exit 1
fi
echo "  JWT Token acquired successfully."

# 5. Perform Storage Operations
echo "[6/6] Performing storage operations on Cafe service..."

FILE_PATH="hello-from-script.txt"
CONTENT="This file was uploaded by the automated demo script at $(date)."

echo "  Uploading file: $FILE_PATH"
curl -s -i -X POST \
     -H "Authorization: Bearer $TOKEN" \
     --data "$CONTENT" \
     "$CAFE_BASE/api/storage/$FILE_PATH" | grep HTTP/1.1

echo "\n  Listing storage directory:"
curl -s -H "Authorization: Bearer $TOKEN" \
     "$CAFE_BASE/api/storage/" | jq .

echo "\n  Reading back the file:"
curl -s -H "Authorization: Bearer $TOKEN" \
     "$CAFE_BASE/api/storage/$FILE_PATH"

echo "\n\n=== Demo Completed Successfully ==="
rm -f "$KEY_FILE"
