import os
import csv
from datetime import datetime
import urllib.request
import json

CSV_PATH = "cashfive.csv"

def get_existing_draws():
    draws = set()
    if not os.path.exists(CSV_PATH):
        return draws

    with open(CSV_PATH, mode='r', encoding='utf-8') as f:
        reader = csv.reader(f)
        for row in reader:
            if len(row) >= 4:
                # row format: GameName, Month, Day, Year, N1, N2, N3, N4, N5
                game = row[0].strip()
                if "Cash Five" in game:
                    try:
                        m = int(row[1])
                        d = int(row[2])
                        y = int(row[3])
                        draws.add((m, d, y))
                    except ValueError:
                        continue
    return draws

def update_csv():
    print("Checking for new Texas Cash 5 drawings...")
    existing_draws = get_existing_draws()
    print(f"Found {len(existing_draws)} existing draws in {CSV_PATH}.")

    # TODO: Connect to Texas Lottery API or scrape latest draws
    # Example structure for appending new draws:
    # new_draws = [
    #    ("Cash Five", month, day, year, n1, n2, n3, n4, n5)
    # ]

    new_draws_added = 0
    # for draw in new_draws:
    #     if (draw[1], draw[2], draw[3]) not in existing_draws:
    #         append to cashfive.csv
    #         new_draws_added += 1

    print(f"Update complete. Added {new_draws_added} new draws.")

if __name__ == "__main__":
    update_csv()
