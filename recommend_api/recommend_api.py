from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from datetime import datetime

app = Flask(__name__)
model = SentenceTransformer('paraphrase-MiniLM-L6-v2')

@app.route('/recommend', methods=['POST'])
def recommend():
    data = request.json
    history = data["history"]
    candidats = data["candidats"]

    print("✅ Reçu !")
    print("🧠 Événements dans historique:", len(history))
    print("🎯 Événements candidats:", len(candidats))

    all_events = history + candidats
    descriptions = []

    for e in all_events:
        event_date = ""
        if "dateEvenement" in e and e["dateEvenement"]:
            try:
                date_obj = datetime.fromisoformat(e["dateEvenement"].replace('Z', '+00:00'))
                event_date = date_obj.strftime("%A %H:%M")
            except:
                pass

        # 🔁 Pondération par répétition (poids sur type + titre)
        description = f"{e['typeEvenement']} {e['typeEvenement']} {e['titre']} {e['titre']} {e['lieu']} {event_date}"

        if "description" in e and e["description"]:
            description += f" {e['description']}"

        descriptions.append(description)

    embeddings = model.encode(descriptions)
    history_vectors = embeddings[:len(history)]
    candidat_vectors = embeddings[len(history):]

    if len(history_vectors) == 0:
        return jsonify([])

    history_mean = sum(history_vectors) / len(history_vectors)
    scores = cosine_similarity([history_mean], candidat_vectors)[0]

    for i, score in enumerate(scores):
        candidats[i]["score"] = float(score)

    candidats_sorted = sorted(candidats, key=lambda x: x["score"], reverse=True)

    for e in candidats_sorted:
        e.pop("score", None)

    print("🔥 Top 3:", [e["titre"] for e in candidats_sorted[:5]])
    return jsonify(candidats_sorted[:3])

if __name__ == '__main__':
    app.run(port=5005)
