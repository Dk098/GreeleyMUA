import argparse, textwrap
import server

def main():
    parser = argparse.ArgumentParser(
        formatter_class=argparse.RawDescriptionHelpFormatter,
        description=textwrap.dedent('''
            GreeleyMUA
            ----------------------------------------------------------
            A backend mail user agent that works with CoffeeMail so users can read their emails
        '''),
        epilog=textwrap.dedent('''

            -created by yasgur99, menachem, and karlguy
        ''')
    )

    args = parser.parse_args()

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("Terminated...")
    except Exception as E:
        print(E)
