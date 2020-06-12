# Installation with : 'pip install recipe-scrapers && pip3 install psycopg2-binary && python3 RecipeWebScraper.py'
# example url: https://www.allrecipes.com/recipe/158968/spinach-and-feta-turkey-burgers/
import psycopg2
from recipe_scrapers import scrape_me


def main():
    connection = makeDBConnection()
    notBreaking = True
    while (notBreaking):

        input_url = input("(write 'q' to quit), Please enter a url to webscrape a recipe from:")
        if (len(input_url) == 0):
            input_url = "https://www.allrecipes.com/recipe/158968/spinach-and-feta-turkey-burgers/"

        if (input_url == "q"):
            notBreaking = False
            closeDBConnection(connection)
        else:
            scrapeRecipe(input_url, connection)


def scrapeRecipe(input_url, connection):
    scraper = scrape_me(input_url)
    print("got recipe info:")
    print(scraper.title())
    print(scraper.total_time())
    print(scraper.yields())
    print(scraper.ingredients())
    print(scraper.instructions())
    print(scraper.image())
    # print(scraper.links())
    input_accepted = input("(enter to save, anything to decline)")
    if (input_accepted == ""):
        cursor = connection.cursor()
        cursor.execute("INSERT INTO public.recipe (user_id) values (null) ")
        connection.commit()
        cursor.close()

        # cursor.execute("SELECT * FROM public.user_profile")
        # rows = cursor.fetchall()
        # for row in rows:
        #     print(f"id {row[0]} ...")


def makeDBConnection():
    print("establish connection to database ...")
    connection = psycopg2.connect(
        host="127.0.0.1",
        database="springmvcohm",
        user="postgres",
        password="postgres",
        port="5432"
    )
    print("connected!")
    return connection


def closeDBConnection(connection):
    connection.close()
    print("database connection closed. Finished.")


main()
