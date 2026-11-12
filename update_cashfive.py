import os
import re
import requests
from bs4 import BeautifulSoup

CSV_FILE = "cashfive.csv"

def fetch_latest_draws():
    url = "https://www.texaslottery.com/export/sites/lottery/Games/Cash_Five/Winning_Numbers/index.html"
    headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"}

    try:
        response = requests.get(url, headers=headers, timeout=15)
        response.raise_for_status()
    except Exception as e:
        print(f"Error fetching Texas Lottery web page: {e}")
        return []

    soup = BeautifulSoup(response.text, "html.parser")
    rows = soup.find_all("tr")
    new_entries = []

    for row in rows:
        cols = row.find_all(['td', 'th'])
        if len(cols) < 2:
            continue

        row_text = row.get_text()
        date_match = re.search(r'(\d{1,2})/(\d{1,2})/(\d{4})', row_text)
        if not date_match:
            continue

        m, d, y = int(date_match.group(1)), int(date_match.group(2)), int(date_match.group(3))

        # Extract numbers specifically from the table columns after the date column (Cash 5 numbers are 1-35)
        numbers = []
        for col in cols[1:]:
            found = re.findall(r'\b([1-9]|[1-2][0-9]|3[0-5])\b', col.get_text())
            for f in found:
                val = int(f)
                if val <= 35 and val != m and val != d and val != y and val != (y % 100):
                    numbers.append(val)

        if len(numbers) >= 5:
            nums = numbers[:5]
            new_entries.append(((y, m, d), f"Cash Five,{m},{d},{y}," + ",".join(str(n) for n in nums)))

    return new_entries

def main():
    fetched = fetch_latest_draws()
    if not fetched:
        print("No draws fetched.")
        return

    fetched.sort(key=lambda item: item[0])

    valid_lines = []
    if os.path.exists(CSV_FILE):
        with open(CSV_FILE, "r", encoding="utf-8") as f:
            for line in f:
                valid_lines.append(line.strip())

    existing_valid = set()
    for line in valid_lines:
        parts = line.split(",")
        if len(parts) >= 9:
            try:
                existing_valid.add((int(parts[3]), int(parts[1]), int(parts[2])))
            except ValueError:
                pass

    added_count = 0
    with open(CSV_FILE, "w", encoding="utf-8") as f:
        for line in valid_lines:
            f.write(line + "\n")
        for date_tuple, csv_line in fetched:
            if date_tuple not in existing_valid:
                f.write(csv_line + "\n")
                print(f"Added new draw: {csv_line}")
                added_count += 1

    print(f"Sync finished. Added {added_count} new draws.")

if __name__ == "__main__":
    main()
