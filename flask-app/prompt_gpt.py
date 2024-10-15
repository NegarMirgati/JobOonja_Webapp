import openai
import os

api_key = os.environ.get("OPENAI_API_KEY")
print(api_key)


def generate_project_description(skills):

    example_project_description = """
    ما به عنوان تیم سیناپس یک تیم بین رشته ای از فارغ‌التحصیلان پزشکی و کامپیوتر هستیم که در زمینه‌ی تکنولوژی در سلامت و هوشمندسازی بیمارستان ها داریم کار می کنیم.
    ما از ۳ سال قبل تا کنون در یک محیط خوب، با تیمی دوستانه و علاقه‌مند به پیشرفت مشغول به توسعه‌ی نرم افزارهای حوزه‌ی سلامت هستیم.
ما برای گسترش تیم برنامه‌نویسی‌مون به افرادی نیاز داریم که به جاوا یا پایتون تا حد خوبی آشنا باشن، به یادگیری و حل مسائل تازه در زمینه‌ی علوم کامپیوتر علاقه داشته باشن و بتونن با تیم به خوبی تعامل کنن.
    """

    # Prompt to ask GPT
    prompt = f"""
        یک پروژه در یک پاراگراف کوتاه تعریف کن که مرتبط با مهارت های {skills} باشد.
        این یک نمونه توضیح پروژه شامل مهارت های Java و Python برای وضوح بیشتر است
        {example_project_description}.
        از جزییات این نمونه کپی برداری نکن. هدف از این نمونه ارائه یک مثال از یک تعریف پروژه واقعی بود. تو می توانی در جزییات خلاقیت به خرج بدهی.
        """
    client = openai.OpenAI(api_key=api_key)

    # Generate a response using GPT
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",  # or "gpt-4" if you have access
        messages=[{"role": "user", "content": prompt}],
        max_tokens=250,  # Limits the length of the response
        n=1,  # Number of responses to generate
        stop=None,
        temperature=0.7,  # Creativity level (0.7 is a good balance)
    )

    # Extract the text from the response
    project_description = response.choices[0].message.content
    return project_description


if __name__ == "__main__":
    # Example usage
    skills = "C++, Photoshop"
    project_description = generate_project_description(skills)
    print(project_description)
