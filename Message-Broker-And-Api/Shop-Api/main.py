import os
import urllib.request
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

app = FastAPI(title="Apex Track Day - Performance Shop API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Maak een map voor lokale afbeeldingen
os.makedirs("static", exist_ok=True)

# Betrouwbare fallback afbeeldingen (downloaden eenmalig naar de container)
IMAGES = {
    "tires.jpg": "https://images.unsplash.com/photo-1578844251758-2f71da64c96f?w=500&q=80",
    "oil.jpg": "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=500&q=80",
    "gear.jpg": "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=500&q=80",
    "brakes.jpg": "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=500&q=80"
}

for filename, url in IMAGES.items():
    filepath = os.path.join("static", filename)
    if not os.path.exists(filepath):
        try:
            urllib.request.urlretrieve(url, filepath)
        except Exception:
            # Als internet op je PC plat ligt, maakt hij een lege dummy file aan zodat de API niet crasht
            with open(filepath, "w") as f:
                f.write("")

# Koppel de statische map aan de API webserver
app.mount("/static", StaticFiles(directory="static"), name="static")

# 💡 De API stuurt nu de lokale URL's terug gericht op de Android Emulator (10.0.2.2)!
MOCK_PRODUCTS = [
    {
        "id": 1,
        "name": "Michelin Pilot Sport Cup 2",
        "category": "Tires",
        "price": 1299.0,
        # 💡 GEFIXT: Volledige HTTPS cloud-URL's. Coil laadt dit overal direct in!
        "imageUrl": "https://s1.medias-auto5.be/images_produits/tyre_comm_txt-michelin_pilot_sport_4s-1/900x900/band-michelin-pilot-sport-4s-285-35-zr19-103-y-xl--2108737.png",
        "isFeatured": True
    },
    {
        "id": 2,
        "name": "Castrol Edge 5W-30",
        "category": "Oil",
        "price": 89.0,
        "imageUrl": "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=600&q=80",
        "isFeatured": False
    },
    {
        "id": 3,
        "name": "Apex Racing Suit - Pro",
        "category": "Gear",
        "price": 549.0,
        "imageUrl": "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=600&q=80",
        "isFeatured": True
    },
    {
        "id": 4,
        "name": "Brembo GT Brake Kit",
        "category": "Brakes",
        "price": 2499.0,
        "imageUrl": "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=600&q=80",
        "isFeatured": False
    }
]
@app.get("/api/products")
async def get_products():
    return MOCK_PRODUCTS